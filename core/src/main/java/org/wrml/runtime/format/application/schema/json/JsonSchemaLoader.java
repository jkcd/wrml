/**
 * WRML - Web Resource Modeling Language
 *  __     __   ______   __    __   __
 * /\ \  _ \ \ /\  == \ /\ "-./  \ /\ \
 * \ \ \/ ".\ \\ \  __< \ \ \-./\ \\ \ \____
 *  \ \__/".~\_\\ \_\ \_\\ \_\ \ \_\\ \_____\
 *   \/_/   \/_/ \/_/ /_/ \/_/  \/_/ \/_____/
 *
 * http://www.wrml.org
 *
 * Copyright (C) 2011 - 2013 Mark Masse <mark@wrml.org> (OSS project WRML.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wrml.runtime.format.application.schema.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.wrml.model.MaybeRequired;
import org.wrml.model.rest.LinkRelation;
import org.wrml.model.schema.*;
import org.wrml.runtime.*;
import org.wrml.runtime.format.application.schema.json.JsonSchema.Definitions.JsonType;
import org.wrml.runtime.format.application.schema.json.JsonSchema.Definitions.PropertyType;
import org.wrml.runtime.format.application.schema.json.JsonSchema.JsonStringFormat;
import org.wrml.runtime.schema.PropertyProtoSlot;
import org.wrml.runtime.schema.ProtoSlot;
import org.wrml.runtime.schema.Prototype;
import org.wrml.runtime.schema.SchemaLoader;
import org.wrml.runtime.syntax.SyntaxLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

public class JsonSchemaLoader implements Loader {

    private Context _Context;

    private final ConcurrentHashMap<URI, JsonSchema> _JsonSchemas;

    public JsonSchemaLoader() {

        _JsonSchemas = new ConcurrentHashMap<URI, JsonSchema>();
    }

    public static void main(final String[] args) throws Exception {

        final String wrmlConfigFilePath = args[0];
        final EngineConfiguration config = EngineConfiguration.load(wrmlConfigFilePath);
        final Engine engine = new DefaultEngine();
        engine.init(config);

        final String urlString = args[1];
        final URL jsonSchemaId = new URL(urlString);

        final SchemaLoader schemaLoader = engine.getContext().getSchemaLoader();

        final JsonSchemaLoader jsonSchemaLoader = schemaLoader.getJsonSchemaLoader();
        final JsonSchema downloadedJsonSchema = jsonSchemaLoader.load(jsonSchemaId);
        System.out.println("Downloaded JSON schema:\n" + downloadedJsonSchema);

        final Schema schema = schemaLoader.load(downloadedJsonSchema, schemaLoader.getDocumentSchemaUri());
        System.out.println("Generated WRML schema:\n" + schema);

        final Schema docSchema = schemaLoader.load(schemaLoader.getDocumentSchemaUri());
        final JsonSchema docJsonSchema = jsonSchemaLoader.load(docSchema);
        System.out.println("Generated WRML's Document schema as a JSON schema:\n" + docJsonSchema);

    }

    public JsonSchema getLoadedJsonSchema(final URI schemaUri) {

        if (_JsonSchemas.containsKey(schemaUri)) {
            return _JsonSchemas.get(schemaUri);
        }
        return null;
    }

    public SortedSet<URI> getLoadedJsonSchemaUris() {

        return new TreeSet<URI>(_JsonSchemas.keySet());
    }


    public JsonSchema load(final File file) throws IOException {

        if (file == null) {
            throw new FileNotFoundException("The JSON schema file is null.");
        }

        if (!file.exists()) {
            throw new FileNotFoundException("The JSON schema file named \"" + file.getAbsolutePath()
                    + "\" does not exist.");
        }

        final InputStream in = FileUtils.openInputStream(file);
        final JsonSchema jsonSchema = load(in, null);
        IOUtils.closeQuietly(in);
        return jsonSchema;
    }

    public JsonSchema load(final InputStream in, final URI jsonSchemaId) throws IOException {

        final ObjectNode rootNode = new ObjectMapper().readValue(in, ObjectNode.class);
        return load(rootNode, jsonSchemaId);
    }

    public JsonSchema load(final ObjectNode rootNode, final URI jsonSchemaId) {

        final URI jsonSchemaIdPropertyValue = PropertyType.Id.getValue(rootNode, getSyntaxLoader());
        final URI id = (jsonSchemaIdPropertyValue != null) ? jsonSchemaIdPropertyValue : jsonSchemaId;

        if (id == null) {
            throw new JsonSchemaLoaderException("The JSON Schema \"id\" slot value (URI) could not be determined.",
                    null, this);

        }

        PropertyType.Id.setValue(rootNode, id, getSyntaxLoader());

        final JsonSchema jsonSchema = new JsonSchema(this, rootNode);

        _JsonSchemas.put(id, jsonSchema);

        return jsonSchema;

    }

    public JsonSchema load(final Schema wrmlSchema) {
        return load(wrmlSchema, false);
    }

    public JsonSchema load(final Schema wrmlSchema, boolean swaggerMode) {

        if (wrmlSchema == null) {
            return null;
        }

        final URI schemaUri = wrmlSchema.getUri();
        if (_JsonSchemas.containsKey(schemaUri)) {
            return _JsonSchemas.get(schemaUri);
        }

        final String schemaTitle = wrmlSchema.getTitle();
        final String schemaDescription = wrmlSchema.getDescription();

        final ObjectNode schemaNode = JsonNodeFactory.instance.objectNode();

        PropertyType.Title.setValue(schemaNode, schemaTitle, getSyntaxLoader());
        PropertyType.Description.setValue(schemaNode, schemaDescription, getSyntaxLoader());
        PropertyType.Type.setValue(schemaNode, JsonType.Object.getKeyword(), getSyntaxLoader());

        // TODO
        // wrmlSchema.getVersion();

        final ObjectNode propertiesNode = schemaNode.putObject(PropertyType.Properties.getName());

        final SchemaLoader schemaLoader = getContext().getSchemaLoader();
        final Prototype prototype = schemaLoader.getPrototype(schemaUri);
        final SortedSet<String> allSlotNames = prototype.getAllSlotNames();
        for (String slotName : allSlotNames) {
            final ProtoSlot protoSlot = prototype.getProtoSlot(slotName);

            if (!protoSlot.getClass().equals(PropertyProtoSlot.class)) {
                continue;
            }

            final URI declaringSchemaUri = protoSlot.getDeclaringSchemaUri();
            final Schema declaringSchema = schemaLoader.load(declaringSchemaUri);
            final List<Slot> declaringSchemaSlots = declaringSchema.getSlots();

            Slot propertySlot = null;
            for (Slot slot : declaringSchemaSlots) {
                if (slotName.equals(slot.getName()))  {
                    propertySlot = slot;
                    break;
                }
            }

            if (propertySlot != null) {
                final ObjectNode slotNode = propertiesNode.putObject(slotName);
                initPropertySlotNode(slotNode, propertySlot, swaggerMode);
            }
        }

        if (swaggerMode) {
            return new JsonSchema(this, schemaNode);
        }
        else {
            return load(schemaNode, schemaUri);
        }
    }

    public JsonSchema load(final URI jsonSchemaUri) throws IOException {

        if (_JsonSchemas.containsKey(jsonSchemaUri)) {
            return _JsonSchemas.get(jsonSchemaUri);
        }

        final ObjectNode rootNode = new ObjectMapper().readValue(jsonSchemaUri.toURL(), ObjectNode.class);
        return load(rootNode, jsonSchemaUri);
    }

    public JsonSchema load(final URL jsonSchemaUrl) throws IOException {

        final URI jsonSchemaUri = URI.create(jsonSchemaUrl.toString());
        if (_JsonSchemas.containsKey(jsonSchemaUri)) {
            return _JsonSchemas.get(jsonSchemaUri);
        }

        final ObjectNode rootNode = new ObjectMapper().readValue(jsonSchemaUrl, ObjectNode.class);
        return load(rootNode, jsonSchemaUri);
    }

    private JsonType getJsonType(final Value value) {

        final JsonType jsonType;

        if (value instanceof TextValue) {
            jsonType = JsonType.String;
        }
        else if (value instanceof ListValue) {
            jsonType = JsonType.Array;
        }
        else if (value instanceof ModelValue) {
            jsonType = JsonType.Object;
        }
        else if (value instanceof IntegerValue) {
            jsonType = JsonType.Integer;
        }
        else if (value instanceof BooleanValue) {
            jsonType = JsonType.Boolean;
        }
        else if (value instanceof DoubleValue || value instanceof LongValue) {
            jsonType = JsonType.Number;
        }
        else if (value instanceof DateValue || value instanceof SingleSelectValue) {
            jsonType = JsonType.String;
        }
        else if (value instanceof LinkValue) {
            jsonType = null;
        }
        else {
            jsonType = JsonType.Any;
        }

        return jsonType;
    }

    /*
    private void initLinkSlotNode(final ObjectNode schemaNode, final Slot slot) {

        ArrayNode linksArrayNode = PropertyType.Links.getValueNode(schemaNode);
        if (linksArrayNode == null) {
            linksArrayNode = schemaNode.putArray(PropertyType.Links.getName());
        }

        final LinkValue linkValue = (LinkValue) slot.getValue();
        final ObjectNode linkNode = linksArrayNode.addObject();
        PropertyType.Type.setValue(linkNode, JsonType.Object.getKeyword(), getSyntaxLoader());

        final URI linkRelationUri = linkValue.getLinkRelationUri();
        final LinkRelation linkRelation = linkValue.getContext().getApiLoader().loadLinkRelation(linkRelationUri);
        if (linkRelation != null) {

            PropertyType.Rel.setValue(linkNode, linkRelation.getUniqueName().getLocalName(), getSyntaxLoader());

            PropertyType.Method
                    .setValue(linkNode, linkRelation.getMethod().getProtocolGivenName(), getSyntaxLoader());
        }

        final URI responseSchemaUri = linkValue.getResponseSchemaUri();
        if (responseSchemaUri != null) {
            final ObjectNode targetSchemaNode = linkNode.putObject(PropertyType.TargetSchema.getName());
            PropertyType.Type.setValue(targetSchemaNode, JsonType.Object.getKeyword(), getSyntaxLoader());
            PropertyType.$Ref.setValue(targetSchemaNode, responseSchemaUri, getSyntaxLoader());
        }

        final URI requestSchemaUri = linkValue.getRequestSchemaUri();
        if (requestSchemaUri != null) {
            final ObjectNode paramSchemaNode = linkNode.putObject(PropertyType.Schema.getName());
            PropertyType.Type.setValue(paramSchemaNode, JsonType.Object.getKeyword(), getSyntaxLoader());
            PropertyType.$Ref.setValue(paramSchemaNode, requestSchemaUri, getSyntaxLoader());
        }

    }
    */

    private void initPropertySlotNode(final ObjectNode slotNode, final Slot slot, final boolean swaggerMode) {

        final SyntaxLoader syntaxLoader = getSyntaxLoader();

        final Value value = slot.getValue();

        PropertyType.Title.setValue(slotNode, slot.getTitle(), syntaxLoader);
        PropertyType.Description.setValue(slotNode, slot.getDescription(), syntaxLoader);
        final JsonType valueJsonType = getJsonType(value);

        if (valueJsonType != null) {
            PropertyType.Type.setValue(slotNode, valueJsonType.getKeyword(), syntaxLoader);
        }

        if (value instanceof TextValue) {
            final TextValue textValue = (TextValue) value;
            final URI syntaxUri = textValue.getSyntaxUri();
            if (syntaxUri != null) {


                final Class<?> syntaxJavaClass = syntaxLoader.getSyntaxJavaClass(syntaxUri);
                if (syntaxJavaClass != null) {
                    final JsonStringFormat jsonStringFormat = JsonStringFormat.forJavaType(syntaxJavaClass);
                    if (jsonStringFormat != null) {
                        PropertyType.Format.setValue(slotNode, jsonStringFormat.getKeyword(), syntaxLoader);
                    }
                }
            }

            PropertyType.MaxLength.setValue(slotNode, value.getSlotValue(TextValue.SLOT_NAME_MAXIMUM_LENGTH),
                    syntaxLoader);

            PropertyType.MinLength.setValue(slotNode, value.getSlotValue(TextValue.SLOT_NAME_MINIMUM_LENGTH),
                    syntaxLoader);

        }
        else if (value instanceof ListValue) {
            final ListValue listValue = (ListValue) value;
            final ArrayNode arrayNode = slotNode.putArray(PropertyType.Items.getName());
            final ObjectNode itemSlotNode = arrayNode.addObject();
            initPropertySlotNode(itemSlotNode, listValue.getElementSlot(), swaggerMode);

            PropertyType.MaxItems.setValue(slotNode, value.getSlotValue(ListValue.SLOT_NAME_MAXIMUM_SIZE),
                    syntaxLoader);

            PropertyType.MinItems.setValue(slotNode, value.getSlotValue(ListValue.SLOT_NAME_MINIMUM_SIZE),
                    syntaxLoader);

            PropertyType.UniqueItems.setValue(slotNode,
                    value.getSlotValue(ListValue.SLOT_NAME_ELEMENT_UNIQUENESS_CONSTRAINED), syntaxLoader);

        }
        else if (value instanceof ModelValue) {
            final ModelValue modelValue = (ModelValue) value;
            final URI modelSchemaUri = modelValue.getModelSchemaUri();
            if (modelSchemaUri != null) {
                String refSlotValue = modelSchemaUri.toString();
                if (swaggerMode) {
                    int lastSlashIndex = refSlotValue.lastIndexOf('/');
                    if (lastSlashIndex > 0 && lastSlashIndex < refSlotValue.length() - 1) {
                        refSlotValue = refSlotValue.substring(lastSlashIndex + 1);
                        refSlotValue = "#/definitions/" + refSlotValue;
                    }
                }
                else {
                    refSlotValue = refSlotValue + "?accept=application/schema%2Bjson";
                }

                PropertyType.$Ref.setValue(slotNode, refSlotValue, syntaxLoader);
            }
        }
        else if (value instanceof NumericValue) {
            PropertyType.Maximum.setValue(slotNode, value.getSlotValue(NumericValue.SLOT_NAME_MAXIMUM),
                    syntaxLoader);

            PropertyType.Minimum.setValue(slotNode, value.getSlotValue(NumericValue.SLOT_NAME_MINIMUM),
                    syntaxLoader);

        }

        if (value instanceof MaybeRequired) {
            PropertyType.Required.setValue(slotNode, value.getSlotValue(MaybeRequired.SLOT_NAME_REQUIRED),
                    syntaxLoader);
        }

        if (value.containsSlotValue(Value.SLOT_NAME_DEFAULT)) {
            PropertyType.Default.setValue(slotNode, value.getSlotValue(Value.SLOT_NAME_DEFAULT), syntaxLoader);
        }

    }

    private SyntaxLoader getSyntaxLoader() {

        return getContext().getSyntaxLoader();
    }

    @Override
    public void loadInitialState() {

    }

    @Override
    public Context getContext() {

        return _Context;
    }

    @Override
    public void init(final Context context) {

        if (context == null) {
            throw new JsonSchemaLoaderException("The context cannot be null.", null, this);
        }

        _Context = context;

    }
}
