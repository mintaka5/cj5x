package org.cj5x.main.node;

import java.util.HashMap;

import spark.Filter;
import spark.Request;
import spark.Response;
import spark.Spark;

public final class CorsFilter {
	private static final HashMap<String, String> headers = new HashMap<String, String>();
	
	static {
		//headers.put("Access-Control-Allow-Methods", "*");
		headers.put("Access-Control-Allow-Origin", "*");
		headers.put("Access-Control-Allow-Headers", "*");
	};
	
	public final static void apply() {
		Filter filter = new Filter() {

			@Override
			public void handle(Request request, Response response) throws Exception {
				headers.forEach((key, value) -> {
					response.header(key, value);
				});
			}
			
		};
		
		Spark.options("/*", (request, response) -> {
			
			headers.forEach((key, value) -> {
				String acr = request.headers(key);
				if(acr != null) {
					response.header(key, value);
				}
			});

	        return "OK";
	    });
		
		Spark.before(filter);
	}
}
