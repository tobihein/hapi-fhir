package ca.uhn.fhir.rest.server.exceptions;

/*
 * #%L
 * HAPI FHIR Library
 * %%
 * Copyright (C) 2014 University Health Network
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

/**
 * RESTful method exception which corresponds to the <b>HTTP 400 Bad Request</b> status.
 * This status indicates that the client's message was invalid (e.g. not a valid FHIR Resource
 * per the specifications), as opposed to the {@link InvalidRequestException} which indicates
 * that data does not pass business rule validation on the server.
 * 
 * @see UnprocessableEntityException Which should be used for business level validation failures
 */
public class InvalidRequestException extends BaseServerResponseException {

	private static final long serialVersionUID = 1L;

	public InvalidRequestException(String theMessage) {
		super(400, theMessage);
	}

}
