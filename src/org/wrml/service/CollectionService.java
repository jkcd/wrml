/**
 * Copyright (C) 2012 WRML.org <mark@wrml.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wrml.service;

import java.util.Set;

import org.wrml.model.Collection;
import org.wrml.model.Document;
import org.wrml.model.DocumentSearchCriteria;
import org.wrml.runtime.Dimensions;

public interface CollectionService extends DocumentService {

    /**
     * Creates the specified document in the identified collection.
     */
    public <D extends Document> D create(Collection<?> collection, D document);

    /**
     * Search the identified collection for a set of documents.
     */
    public <D extends Document> Set<D> search(Collection<?> collection, DocumentSearchCriteria criteria,
            Dimensions dimensions);
}