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
package jaeger.resource.assembler;

import jaeger.controller.DocumentController;
import jaeger.exception.ResourceNotFoundException;
import jaeger.exception.UnauthorizedException;
import jaeger.model.Context;
import jaeger.model.Document;
import jaeger.resource.DocumentResource;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * @author James Renfro
 */
public class DocumentResourceAssembler extends ResourceAssemblerSupport<Document, DocumentResource> {

    private final Context context;

    public DocumentResourceAssembler() {
        this(null);
    }

    public DocumentResourceAssembler(Context context) {
        super(DocumentController.class, DocumentResource.class);
        this.context = context;
    }

    @Override
    public DocumentResource toResource(Document document) {
        DocumentResource resource = new DocumentResource(document, context);
        try {
            resource.add(linkTo(methodOn(DocumentController.class).read(document.getDocumentId())).withSelfRel());
        } catch (Exception e) {
            // This won't actually happen, since we're not executing the read method above
        }
        return resource;
    }

}
