package iris.client_bff.kir_tracing.eps;

/**
 * @author Jens Kutzsche
 */
public interface KirTracingTestData {

	String JSON_RPC_REQUEST = """
			{
			    "id":"1",
			    "jsonrpc":"2.0",
			    "method":"requestKirConnection",
			    "params":{
			    		"_client":{"name":"hd-1"},
			    		%s
			    }
			}
			""";

	String JSON_RPC_SUBMIT_REQUEST = """
			{
			    "id":"1",
			    "jsonrpc":"2.0",
			    "method":"submitKirTracingForm",
			    "params":{
			    		"_client":{"name":"hd-1"},
			    		%s
			    }
			}
			""";

	String JSON_RPC_CHALLENGE_REQUEST = """
			{
			    "id":"1",
			    "jsonrpc":"2.0",
			    "method":"challengeKir",
			    "params":{
			    		"_client":{"name":"hd-1"},
			    		%s
			    }
			}
			""";

	String JSON_RPC_ACCESS_TOKEN_REQUEST = """
			{
			    "id":"1",
			    "jsonrpc":"2.0",
			    "method":"generateKirAccessToken",
			    "params":{
			    		"_client":{"name":"hd-1"},
			    		%s
			    }
			}
			""";

	String JSON_RPC_SUBMIT_THERAPY_REQUEST = """
			{
			    "id":"1",
			    "jsonrpc":"2.0",
			    "method":"submitKirTherapyResults",
			    "params":{
			    		"_client":{"name":"hd-1"},
			    		%s
			    }
			}
			""";

	String JSON_RPC_AUTHORIZE_REQUEST = """
			{
			    "id":"1",
			    "jsonrpc":"2.0",
			    "method":"authorizeKir",
			    "params":{
			    		"_client":{"name":"hd-1"},
			    		%s
			    }
			}
			""";

	String VALID_CONNECTION_REQUEST = String.format(JSON_RPC_REQUEST, """
			"connectionData":{
				"submitterPublicKey":"%s"
			}
			""");

	String VALID_FORM_SUBMISSION_REQUEST = String.format(JSON_RPC_SUBMIT_REQUEST, """
			"dataAuthorizationToken":"%s",
			"salt":"%s",
			"verifier":"%s",
			"accessToken": "%s",
			"form":{
				"person": {
					"mobilePhone":"+4915147110815"
				},
				"assessment": {
					"form": {
						"key":"value"
					}
				}
				
			}
			""");

	String VALID_CHALLENGE_REQUEST = String.format(JSON_RPC_CHALLENGE_REQUEST, """
			"dataAuthorizationToken":"%s",
			"accessToken":"%s"
			""");

	String VALID_GENERATE_ACCESS_TOKEN_REQUEST = String.format(JSON_RPC_ACCESS_TOKEN_REQUEST, """
			"dataAuthorizationToken":"%s"
			""");

	String VALID_AUTHORIZE_REQUEST = String.format(JSON_RPC_AUTHORIZE_REQUEST, """
			"dataAuthorizationToken":"%s",
			"a": "%s",
			"m1": "%s",
			"accessToken":"%s"
			""");

	String VALID_SUBMIT_THERAPY_REQUEST = String.format(JSON_RPC_SUBMIT_THERAPY_REQUEST, """
			"dataAuthorizationToken":"%s",
			"a": "%s",
			"m1": "%s",
			"accessToken":"%s",
			"form":{
			"therapyResults": {
					"successful":true
				}
			}
			""");

	String ANNOUNCEMENT_WITHOUT_VALUES = String.format(JSON_RPC_REQUEST, """
			"announcementData":{
			}
			""");

	String ANNOUNCEMENT_WITH_EMPTY_VALUES = String.format(JSON_RPC_REQUEST, """
			"announcementData":{
				"externalId":"",
				"submitterPublicKey":"   "
			}
			""");

	String ANNOUNCEMENT_WITH_FORBIDDEN_VALUES = String.format(JSON_RPC_REQUEST, """
			"announcementData":{
				"externalId":"@auieiuae",
				"submitterPublicKey":"uaietrn<SCRIPT >auieuiae"
			}
			""");
}
