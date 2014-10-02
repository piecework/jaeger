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
package jaeger.controller;

import jaeger.Utility;
import jaeger.enumeration.AccessLevel;
import jaeger.exception.ResourceNotFoundException;
import jaeger.exception.UnauthorizedException;
import jaeger.model.*;
import jaeger.repository.ContextRepository;
import jaeger.repository.DocumentRepository;
import jaeger.resource.DocumentResource;
import jaeger.resource.assembler.DocumentResourceAssembler;
import jaeger.security.AccessChecker;
import jaeger.security.IdentityHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartRequest;

import java.util.List;
import java.util.Map;

/**
 * @author James Renfro
 */
@RestController
@ExposesResourceFor(DocumentResource.class)
@RequestMapping("v1/data")
public class DocumentController {

    @Autowired
    private AccessChecker accessChecker;

    @Autowired
    private ContextRepository contextRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private IdentityHelper identityHelper;


    @RequestMapping(value = "{documentId}/{contextId}", method = { RequestMethod.POST, RequestMethod.PUT }, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public DocumentResource attach(@PathVariable("documentId") String documentId, @PathVariable("contextId") String contextId, @RequestBody MultipartRequest request) throws ResourceNotFoundException, UnauthorizedException {
        Context context = getContext(contextId);
        Document document = getDocument(documentId);

        if (accessChecker.check(document, context, identityHelper.getPrincipal()) != AccessLevel.EDIT)
            throw new UnauthorizedException();

        request.getMultiFileMap();

//        Map<String, List<String>> filteredData = Utility.filter(data, context, true);
//
//        Document document = documentRepository.update(documentId, filteredData);

        // TODO: Loop through the attached files and check the context to see if there are fields for them
        // TODO: if there are, then set them as File values, otherwise, if the context allows attachments,
        // TODO: set them as attachments

        DocumentResourceAssembler documentResourceAssembler = new DocumentResourceAssembler(context);
        return documentResourceAssembler.toResource(document);
    }

    /**
     * Retrieves the document with it's default context
     *
     * @param documentId
     * @return
     * @throws ResourceNotFoundException
     */
    @RequestMapping(value = "{documentId}", method = RequestMethod.GET)
    public DocumentResource read(@PathVariable("documentId") String documentId) throws ResourceNotFoundException, UnauthorizedException {
        // Use the document id as the default context id
        return read(documentId, documentId);
    }

    /**
     * Retrieves the document with a specified context
     *
     * @param documentId
     * @param contextId
     * @return
     * @throws ResourceNotFoundException
     */
    @RequestMapping(value = "{documentId}/{contextId}", method = RequestMethod.GET)
    public DocumentResource read(@PathVariable("documentId") String documentId, @PathVariable("contextId") String contextId) throws ResourceNotFoundException, UnauthorizedException {
        Context context = getContext(contextId);
        Document document = getDocument(documentId);

        if (accessChecker.check(document, context, identityHelper.getPrincipal()) == AccessLevel.NONE)
            throw new UnauthorizedException();

        DocumentResourceAssembler documentResourceAssembler = new DocumentResourceAssembler(context);
        return documentResourceAssembler.toResource(document);
    }

    @RequestMapping(method = RequestMethod.GET)
    public PagedResources<DocumentResource> search(
            @RequestParam(defaultValue = "20", required = false, value = "size") Integer size,
            @RequestParam(defaultValue = "0", required = false, value = "page") Integer pageNumber) throws ResourceNotFoundException {

        Page<Document> page = documentRepository.findAll(Utility.pageable(size, pageNumber));
        return Utility.pagedResources(page, new DocumentResourceAssembler());
    }

    /**
     * Updates the data on a document using a map of values
     *
     * @param documentId
     * @param contextId
     * @param data
     * @return
     * @throws ResourceNotFoundException
     * @throws UnauthorizedException
     */
    @RequestMapping(value = "{documentId}/{contextId}", method = { RequestMethod.POST, RequestMethod.PUT }, consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE })
    public DocumentResource update(@PathVariable("documentId") String documentId, @PathVariable("contextId") String contextId, @RequestBody MultiValueMap<String, String> data) throws ResourceNotFoundException, UnauthorizedException {
        Context context = getContext(contextId);
        Document document = getDocument(documentId);

        if (accessChecker.check(document, context, identityHelper.getPrincipal()) != AccessLevel.EDIT)
            throw new UnauthorizedException();

        Map<String, List<String>> filteredData = Utility.filter(data, context, true);

        document = documentRepository.update(documentId, filteredData);

        DocumentResourceAssembler documentResourceAssembler = new DocumentResourceAssembler(context);
        return documentResourceAssembler.toResource(document);
    }

    private Context getContext(String contextId) throws ResourceNotFoundException {
        Context context = contextRepository.findOne(contextId);

        // Handle the case where no default context is defined
        if (context == null)
            throw new ResourceNotFoundException();

        return context;
    }

    private Document getDocument(String documentId) throws ResourceNotFoundException {
        // Read the document from the repository
        Document document = documentRepository.findOne(documentId);

        // Handle the case where no document of that id is found
        if (document == null)
            throw new ResourceNotFoundException();

        return document;
    }


}