/*
 *  Copyright 2014 University of Washington Licensed under the
 *	Educational Community License, Version 2.0 (the "License"); you may
 *	not use this file except in compliance with the License. You may
 *	obtain a copy of the License at
 *
 *  http://www.osedu.org/licenses/ECL-2.0
 *
 *	Unless required by applicable law or agreed to in writing,
 *	software distributed under the License is distributed on an "AS IS"
 *	BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 *	or implied. See the License for the specific language governing
 *	permissions and limitations under the License.
 */
package jaeger.repository;

import jaeger.enumeration.PrincipalType;
import jaeger.model.Permission;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @author James Renfro
 */
public interface PermissionRepository extends MongoRepository<Permission, String> {

    List<Permission> findByPrincipalTypeAndPrincipalId(PrincipalType principalType, String principalId);

}
