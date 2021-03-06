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
package org.wrml.runtime.rest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class RestUtilsTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestUtilsTest.class);

    @Before
    public void init() {

    }

    @After
    public void destruct() {

    }

    // No params
    // @Test
    public void testSimpleContentType1() {

        String contentType = "Content-Type: text/plain";
        MediaType type = RestUtils.extractMediaTypeFromContentTypeHeaderValue(contentType);

        LOGGER.debug("Parsed media type is : " + type);

        assertTrue(type.getType().equals("text"));
        assertTrue(type.getSubType().equals("plain"));
        assertTrue(type.getParameters().isEmpty());
    }

    // No params
    // @Test
    public void testSimpleContentType2() {

        String contentType = "Content-Type: text/plain;";
        MediaType type = RestUtils.extractMediaTypeFromContentTypeHeaderValue(contentType);

        LOGGER.debug("Parsed media type is : " + type);

        assertTrue(type.getType().equals("text"));
        assertTrue(type.getSubType().equals("plain"));
        assertTrue(type.getParameters().isEmpty());
    }

    // One parameter
    // @Test
    public void testSimpleContentType3() {

        String contentType = "Content-Type: text/plain; charset=us-ascii";
        MediaType type = RestUtils.extractMediaTypeFromContentTypeHeaderValue(contentType);

        LOGGER.debug("Parsed media type is : " + type);

        assertTrue(type.getType().equals("text"));
        assertTrue(type.getSubType().equals("plain"));
        assertTrue(!type.getParameters().isEmpty());
        assertTrue(type.getParameters().size() == 1);
        assertTrue(type.getParameter("charset").equals("us-ascii"));
    }

    // Three (n) parameters
    // @Test
    public void testContentType1() {

        String contentType = "Content-Type: text/plain; charset=us-ascii;ben=test; space=\"aliens\"";
        MediaType type = RestUtils.extractMediaTypeFromContentTypeHeaderValue(contentType);

        LOGGER.debug("Parsed media type is : " + type);

        assertTrue(type.getType().equals("text"));
        assertTrue(type.getSubType().equals("plain"));
        assertTrue(!type.getParameters().isEmpty());
        assertTrue("Expected size of 3 but got " + type.getParameters().size(), type.getParameters().size() == 3);
        assertTrue(type.getParameter("charset").equals("us-ascii"));
        assertTrue(type.getParameter("ben").equals("test"));
        assertTrue("Expected a value of aliens but got " + type.getParameter("space"), type.getParameter("space").equals("aliens"));
    }

    // Three (n) parameters
    // @Test
    public void testContentType2() {

        String contentType = "Content-Type: text/plain; charset=us-ascii;ben=test; space=\"ali;ens\"";
        MediaType type = RestUtils.extractMediaTypeFromContentTypeHeaderValue(contentType);

        LOGGER.debug("Parsed media type is : " + type);

        assertTrue(type.getType().equals("text"));
        assertTrue(type.getSubType().equals("plain"));
        assertTrue(!type.getParameters().isEmpty());
        assertTrue("Expected size of 3 but got " + type.getParameters().size(), type.getParameters().size() == 3);
        assertTrue(type.getParameter("charset").equals("us-ascii"));
        assertTrue(type.getParameter("ben").equals("test"));
        assertTrue("Expected a value of aliens but got " + type.getParameter("space"), type.getParameter("space").equals("ali;ens"));
    }

    // @Test
    public void testSimpleAccepts1() {

        String accepts = "Accept: audio/*";
        List<MediaType> types = RestUtils.extractMediaTypesFromAcceptHeaderValue(accepts);

        LOGGER.debug("Parsed media type is : " + types);

        assertTrue(types != null);
        assertTrue(types.size() == 1);
        MediaType type = types.get(0);
        assertTrue(type.getType().equals("audio"));
        assertTrue(type.getSubType().equals("*"));
        assertTrue(type.getParameters().isEmpty());
    }

    // @Test
    public void testSimpleAccepts2() {

        String accepts = "Accept: audio/*;";
        List<MediaType> types = RestUtils.extractMediaTypesFromAcceptHeaderValue(accepts);

        LOGGER.debug("Parsed media type is : " + types);

        assertTrue(types != null);
        assertTrue(types.size() == 1);
        MediaType type = types.get(0);
        assertTrue(type.getType().equals("audio"));
        assertTrue(type.getSubType().equals("*"));
        assertTrue(type.getParameters().isEmpty());
    }

    @Test
    public void testAccepts1() {

        String accepts = "Accept: audio/*; q=0.2; z=35, audio/basic, audio/ben;crazy";
        List<MediaType> types = RestUtils.extractMediaTypesFromAcceptHeaderValue(accepts);

        LOGGER.debug("Parsed media type is : " + types);

        assertTrue(types != null);
        assertTrue(types.size() == 3);

        MediaType type = types.get(0);
        assertTrue(type.getType().equals("audio"));
        assertTrue(type.getSubType().equals("basic"));
        assertTrue(type.getParameters().isEmpty());

        type = types.get(1);
        assertTrue(type.getType().equals("audio"));
        assertTrue(type.getSubType().equals("ben"));
        assertTrue(type.getParameters().size() == 1);
        assertTrue(type.getParameter("crazy").equals(""));

        type = types.get(2);
        assertTrue(type.getType().equals("audio"));
        assertTrue(type.getSubType().equals("*"));
        assertTrue(type.getParameters().size() == 2);
        assertTrue(type.getParameter("q").equals("0.2"));
        assertTrue(type.getParameter("z").equals("35"));
    }

    @Test
    public void testLongAccepts1() {

        String accepts = "Accept: text/*;q=0.3, text/html;q=0.7, text/html;level=1,text/html;level=2;q=0.4, */*;q=0.5";
        List<MediaType> types = RestUtils.extractMediaTypesFromAcceptHeaderValue(accepts);

        LOGGER.debug("Parsed media type is : " + types);

        assertTrue(types != null);
        assertTrue(types.size() == 5);

        MediaType type;

        type = types.get(0);
        assertTrue(type.getType().equals("text"));
        assertTrue(type.getSubType().equals("html"));
        assertTrue(type.getParameters().size() == 1);
        assertTrue(null == type.getParameter("crazy"));

        type = types.get(1);
        assertTrue(type.getType().equals("text"));
        assertTrue(type.getSubType().equals("html"));
        assertTrue(type.getParameters().size() == 1);
        assertTrue(type.getParameter("q").equals("0.7"));

        type = types.get(2);
        assertTrue(type.getType().equals("*"));
        assertTrue(type.getSubType().equals("*"));
        assertTrue(type.getParameters().size() == 1);
        assertTrue(type.getParameter("q").equals("0.5"));

        type = types.get(3);
        assertTrue(type.getType().equals("text"));
        assertTrue(type.getSubType().equals("html"));
        assertTrue(type.getParameters().size() == 2);
        assertTrue(type.getParameter("level").equals("2"));
        assertTrue(type.getParameter("q").equals("0.4"));

        type = types.get(4);
        assertTrue(type.getType().equals("text"));
        assertTrue(type.getSubType().equals("*"));
        assertTrue(type.getParameters().size() == 1);
        assertTrue(type.getParameter("q").equals("0.3"));
    }

    // @Test
    public void wrmlContentTypeTest1() {

        String contentType = "Content-Type: application/wrml; schema=http://schema.api.wrml.org/org/wrml/example/model/Caprica;format=\"http://format.api.wrml.org/org/wrml/format/application/json\"";
        MediaType type = RestUtils.extractMediaTypeFromContentTypeHeaderValue(contentType);

        LOGGER.debug("Parsed media type is : " + type);

        assertTrue(type.getType().equals("application"));
        assertTrue(type.getSubType().equals("wrml"));
        assertTrue(type.getParameters().size() == 2);
        assertTrue(type.getParameter("schema").equals("http://schema.api.wrml.org/org/wrml/example/model/Caprica"));
        assertTrue(type.getParameter("format").equals("http://format.api.wrml.org/org/wrml/format/application/json"));
    }

    @Test
    public void wrmlAcceptTest1() {

        String accepts = "Accept: application/wrml; schema=http://schema.api.wrml.org/org/wrml/example/model/Caprica;format=\"http://format.api.wrml.org/org/wrml/format/application/json\"";
        List<MediaType> types = RestUtils.extractMediaTypesFromAcceptHeaderValue(accepts);

        LOGGER.debug("Parsed media type is : " + types);

        MediaType type;

        type = types.get(0);
        assertTrue(type.getType().equals("application"));
        assertTrue(type.getSubType().equals("wrml"));
        assertTrue(type.getParameters().size() == 2);
        assertTrue(type.getParameter("schema").equals("http://schema.api.wrml.org/org/wrml/example/model/Caprica"));
        assertTrue(type.getParameter("format").equals("http://format.api.wrml.org/org/wrml/format/application/json"));
    }
}
