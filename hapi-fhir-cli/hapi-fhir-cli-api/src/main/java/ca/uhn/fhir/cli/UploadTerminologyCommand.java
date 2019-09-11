package ca.uhn.fhir.cli;

/*-
 * #%L
 * HAPI FHIR - Command Line Client - API
 * %%
 * Copyright (C) 2014 - 2019 University Health Network
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

import ca.uhn.fhir.jpa.term.IHapiTerminologyLoaderSvc;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;
import ca.uhn.fhir.util.ParametersUtil;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.IOUtils;
import org.hl7.fhir.instance.model.api.IBaseParameters;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class UploadTerminologyCommand extends BaseCommand {
	// TODO: Don't use qualified names for loggers in HAPI CLI.
	private static final org.slf4j.Logger ourLog = org.slf4j.LoggerFactory.getLogger(UploadTerminologyCommand.class);
	private static final String UPLOAD_EXTERNAL_CODE_SYSTEM = "upload-external-code-system";

	@Override
	public String getCommandDescription() {
		return "Uploads a terminology package (e.g. a SNOMED CT ZIP file) to a server, using the $" + UPLOAD_EXTERNAL_CODE_SYSTEM + " operation.";
	}

	@Override
	public String getCommandName() {
		return "upload-terminology";
	}

	@Override
	public Options getOptions() {
		Options options = new Options();

		addFhirVersionOption(options);
		addBaseUrlOption(options);
		addRequiredOption(options, "u", "url", true, "The code system URL associated with this upload (e.g. " + IHapiTerminologyLoaderSvc.SCT_URI + ")");
		addOptionalOption(options, "d", "data", true, "Local file to use to upload (can be a raw file or a ZIP containing the raw file)");
		addOptionalOption(options, null, "custom", false, "Indicates that this upload uses the HAPI FHIR custom external terminology format");
		addOptionalOption(options, "m", "mode", true, "The upload mode: SNAPSHOT (default), ADD, REMOVE");
		addBasicAuthOption(options);
		addVerboseLoggingOption(options);

		return options;
	}

	@Override
	public void run(CommandLine theCommandLine) throws ParseException {
		parseFhirContext(theCommandLine);

		ModeEnum mode;
		String modeString = theCommandLine.getOptionValue("m", "SNAPSHOT");
		try {
			mode = ModeEnum.valueOf(modeString);
		} catch (IllegalArgumentException e) {
			throw new ParseException("Invalid mode: " + modeString);
		}

		String termUrl = theCommandLine.getOptionValue("u");
		if (isBlank(termUrl)) {
			throw new ParseException("No URL provided");
		}

		String[] datafile = theCommandLine.getOptionValues("d");
		if (datafile == null || datafile.length == 0) {
			throw new ParseException("No data file provided");
		}

		IGenericClient client = super.newClient(theCommandLine);
		IBaseParameters inputParameters = ParametersUtil.newInstance(myFhirCtx);

		if (theCommandLine.hasOption(VERBOSE_LOGGING_PARAM)) {
			client.registerInterceptor(new LoggingInterceptor(true));
		}

		switch (mode) {
			case SNAPSHOT:
				uploadSnapshot(inputParameters, termUrl, datafile, theCommandLine, client);
				break;
			case ADD:

				for (String next : datafile) {
//					String contents = IOUtils.toString()
				}


				break;
			case REMOVE:
				break;
		}

	}

	private void uploadSnapshot(IBaseParameters theInputparameters, String theTermUrl, String[] theDatafile, CommandLine theCommandLine, IGenericClient theClient) {
		ParametersUtil.addParameterToParametersUri(myFhirCtx, theInputparameters, "url", theTermUrl);
		for (String next : theDatafile) {
			ParametersUtil.addParameterToParametersString(myFhirCtx, theInputparameters, "localfile", next);
		}
		if (theCommandLine.hasOption("custom")) {
			ParametersUtil.addParameterToParametersCode(myFhirCtx, theInputparameters, "contentMode", "custom");
		}

		ourLog.info("Beginning upload - This may take a while...");

		IBaseParameters response = theClient
			.operation()
			.onType(myFhirCtx.getResourceDefinition("CodeSystem").getImplementingClass())
			.named(UPLOAD_EXTERNAL_CODE_SYSTEM)
			.withParameters(theInputparameters)
			.execute();

		ourLog.info("Upload complete!");
		ourLog.info("Response:\n{}", myFhirCtx.newXmlParser().setPrettyPrint(true).encodeResourceToString(response));
	}

	private enum ModeEnum {
		SNAPSHOT, ADD, REMOVE
	}

}
