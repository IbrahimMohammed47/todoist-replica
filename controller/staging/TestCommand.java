package core;

import org.json.JSONObject;

public class TestCommand extends Command {

	private static double classVersion = 2.0;
	@Override
	public void execute() throws Exception {
		System.out.println("this is a test command 3");
		JSONObject res = new JSONObject();
		res.put("answer", "this is a test command");
		res.put("statusCode", 200);
		respond(res.toString());
	}
	
	@Override
	public double getVersion() {
		// TODO Auto-generated method stub
		return classVersion;
	}
}


