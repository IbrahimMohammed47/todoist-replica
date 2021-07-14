package core;

import org.json.JSONObject;

public class ErrorCommand extends Command {

	
	@Override
	public void execute() throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public double getVersion() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void sendErrorMsg(String msg, int status){
		JSONObject res = new JSONObject();
		res.put("err", msg);
		res.put("statusCode", status);

		respond(res.toString());
	}
}
