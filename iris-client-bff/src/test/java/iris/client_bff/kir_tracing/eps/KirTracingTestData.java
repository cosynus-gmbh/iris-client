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

	String JSON_RPC_UPDATE_REQUEST = """
			{
			    "id":"1",
			    "jsonrpc":"2.0",
			    "method":"updateKirTracingForm",
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
			"password":"%s",
			"form":{
				"person": {
					"mobilePhone":"+4915147110815"
				}
			}
			""");

	String VALID_FORM_UPDATE_REQUEST = String.format(JSON_RPC_UPDATE_REQUEST, """
			"dataAuthorizationToken":"%s",
			"password":"%s",
			"accessToken":"%s",
			"form":{
			"person": {
					"mobilePhone":"+4915147110815"
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
