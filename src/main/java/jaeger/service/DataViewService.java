/*
 * Copyright 2013 University of Washington
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jaeger.service;

import jaeger.enumeration.ContextType;
import jaeger.enumeration.ValidationType;
import jaeger.exception.ValidationException;
import jaeger.model.Context;
import jaeger.model.Document;
import jaeger.model.Entity;
import jaeger.repository.ContextRepository;
import jaeger.repository.DocumentRepository;
import jaeger.resource.DataView;
import jaeger.resource.assembler.DataViewAssembler;
import jaeger.validation.Validation;
import jaeger.validation.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * @author James Renfro
 */
public class DataViewService {

    @Autowired
    private ContextRepository contextRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private ValidationService validationService;


    public DataView get(Set<String> allowedNamespaces, Set<String> allowedDocumentIds, String contextId, String documentId) {
        Context context = contextRepository.findOne(contextId);
        Document document = documentRepository.findOne(documentId);

        if (document != null && context != null
                && context.getNamespace().equals(document.getNamespace())
                && isDocumentAllowed(allowedNamespaces, allowedDocumentIds, document)) {
            DataViewAssembler assembler = new DataViewAssembler(context);
            return assembler.toResource(document);
        }

        return null;
    }

    public <T> DataView update(Set<String> allowedNamespaces, Set<String> allowedDocumentIds, String contextId, String documentId, Map<String, List<T>> data, ValidationType validationType, Entity entity) throws ValidationException {
        Context context = contextRepository.findOne(contextId);
        Document document = documentRepository.findOne(documentId);

        if (document != null && context != null
                && context.getNamespace().equals(document.getNamespace())
                && isDocumentAllowed(allowedNamespaces, allowedDocumentIds, document)) {
            Validation validation = validationService.validate(document, context, data, validationType, entity);

            if (validation.isHasError())
                throw new ValidationException(validation);

            DataViewAssembler assembler = new DataViewAssembler(context);
            return assembler.toResource(document);
        }

        return null;
    }

    public List<DataView> search(Set<String> allowedNamespaces) {
        List<DataView> views = new ArrayList<DataView>();
        List<Context> contexts = contextRepository.findByNamespacesAndTypes(allowedNamespaces, Collections.singleton(ContextType.SEARCH));
        List<Document> documents = documentRepository.findByNamespaces(allowedNamespaces);

        Map<String, DataViewAssembler> namespaceAssemblerMap = new HashMap<String, DataViewAssembler>();
        if (contexts != null && !contexts.isEmpty()) {
            for (Context context : contexts) {
                namespaceAssemblerMap.put(context.getNamespace(), new DataViewAssembler(context));
            }

            for (Document document : documents) {
                DataViewAssembler assembler = namespaceAssemblerMap.get(document.getNamespace());
                if (assembler != null) {
                    views.add(assembler.toResource(document));
                }
            }
            return views;
        }

        return Collections.emptyList();
    }

    private boolean isDocumentAllowed(Set<String> allowedNamespaces, Set<String> allowedDocumentIds, Document document) {
        return allowedNamespaces.contains(document.getNamespace()) || allowedDocumentIds.contains(document.getDocumentId());
    }

}
