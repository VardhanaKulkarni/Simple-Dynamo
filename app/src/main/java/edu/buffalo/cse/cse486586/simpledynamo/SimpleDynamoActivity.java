package edu.buffalo.cse.cse486586.simpledynamo;

import android.os.Bundle;
import android.app.Activity;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.widget.TextView;
import android.telephony.TelephonyManager;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import android.util.Log;
import android.os.AsyncTask;
import android.os.Handler;

public class SimpleDynamoActivity extends Activity {

	static final String TAG = SimpleDynamoActivity.class.getSimpleName();
	static final String[] REMOTE_PORT = {"11108","11112","11116","11120","11124"};
	static final int SERVER_PORT = 10000;
    static String successor = null;
	static String predessor = null;
	static String sec_successor = null;
	static String pre_predessor = null;
	static SimpleDynamoProvider contProv = new SimpleDynamoProvider();
	final Uri mUri = buildUri("content", "edu.buffalo.cse.cse486586.simpledynamo.provider");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_simple_dynamo);

		TelephonyManager tel = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		String portStr = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);
		//String portStr = "11108";
		final String myPort = String.valueOf((Integer.parseInt(portStr) * 2));
		Log.e("oncreate", "port " + myPort);
		Context context = getApplicationContext();
		if(context==null){
			Log.e("Oh No","Null AGain");
		}else{
			Log.e("Context","Not Null");
		}

		contProv.socketCreate(myPort,context);
		String suc_Pred= contProv.setChainOrder();
		successor = suc_Pred.split("%")[0];
		predessor = suc_Pred.split("%")[1];
		sec_successor = suc_Pred.split("%")[2];
		pre_predessor = suc_Pred.split("%")[3];
		contProv.initHashMap();
		new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, myPort, successor, predessor, sec_successor, pre_predessor);


		TextView tv = (TextView) findViewById(R.id.textView1);
		tv.setMovementMethod(new ScrollingMovementMethod());
		//findViewById(R.id.button3).setOnClickListener(
		//		new OnTestClickListener(tv, getContentResolver()));
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.simple_dynamo, menu);
        return true;
    }


    private Uri buildUri(String scheme, String authority) {
		Uri.Builder uriBuilder = new Uri.Builder();
		uriBuilder.authority(authority);
		uriBuilder.scheme(scheme);
		return uriBuilder.build();
	}

	protected void onProgressUpdate(String... strings) {
            /*
             * The following code displays what is received in doInBackground().
             */
		String strReceived = strings[0].trim();
		TextView remoteTextView = (TextView) findViewById(R.id.textView1);
		remoteTextView.append(strReceived+"\n");

		return;
	}

	private class ClientTask extends AsyncTask<String, Void, Void> {
		@Override
		protected Void doInBackground(String... msgs) {
			String myP = msgs[0];
			successor = msgs[1];
			predessor = msgs[2];
			sec_successor = msgs[3];
			pre_predessor = msgs[4];
			Log.e("CLIENTTAST",successor+" "+predessor+" "+sec_successor+" "+ pre_predessor);

			sendMsg(predessor, myP);
			sendMsg(successor, myP);
			
			contProv.setRecovery();


			return null;
		}
	}

	public void sendMsg(String portName,String myP){
		try {
			Socket socket = new Socket();
			socket.connect(new InetSocketAddress(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
					Integer.parseInt(portName)));
			PrintWriter out = new PrintWriter(socket.getOutputStream());
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out.println("ALIVE" + "%" + myP);
			out.flush();
			String reply = in.readLine();
			

			if(reply!= null){
				Log.e("Recovery","starting recovery after getti/Sng reply for alive");
				Log.e("Recovery predessor1",predessor);
				Log.e("Recovery pre predessor1",pre_predessor);
				Log.e("Recovery premyport",myP);
				Log.e("Recovery message sent","RECOVER"+"%"+myP+"%"+predessor+"%"+pre_predessor+ portName);
				String sec_reply = socketMsg("RECOVER"+"%"+myP+"%"+predessor+"%"+pre_predessor,portName);

				if(sec_reply == null){
					Log.e("Recovery","null reply " + sec_reply);
				}
				if (sec_reply != null) {
					Log.e("Recovery", "got the messages " + sec_reply + " * ");
					if (!sec_reply.equals("NO")) {
						Log.e("Recovery", "saving messages calling function");
						//saveRecovery("SAVE" + "%" + sec_reply, myP);
						String msgR = "SAVE"+"%"+sec_reply;
						String[] toSavemsg = msgR.split("%");
						Log.e("SAVE", "entered");
						Context context1 = getApplicationContext();
						if(context1==null)
							Log.e("saving context","null");

						for(int i=1;i<toSavemsg.length;i++){
							Log.e("Saving the message ",toSavemsg[i]+ " ");
							String[] temp = toSavemsg[i].split("-");
							String name = temp[0];
							String value = temp[1];
							String portt = temp[2];
							contProv.storeContentProvider(name, value, portt, context1);
						}
					}
				}
			}
			out.close();
			socket.close();

		} catch (IOException e) {
			Log.e(TAG, "IOException in Activity"+ " "+e.toString());
		}
	}


	public void saveRecovery(String msg,String myport){
		try {
			Socket socket = new Socket();
			socket.connect(new InetSocketAddress(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
					Integer.parseInt(myport)));
			PrintWriter out = new PrintWriter(socket.getOutputStream());
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out.println(msg);
			Log.e("from client", "msg sent");
			out.flush();

			out.close();
		}catch(IOException e){

		}
	}

	public String socketMsg(String msg,String port){
		String reply = null;
		try {
			Socket socket = new Socket();
			socket.connect(new InetSocketAddress(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
					Integer.parseInt(port)));
			PrintWriter out = new PrintWriter(socket.getOutputStream());
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out.println(msg);
			Log.e("from client", "msg sent");
			out.flush();
			reply = in.readLine();
			Log.e("reply from recover", "**" +reply+ " && ");
			out.close();
			return reply;
		}catch(IOException e){

		}
		return null;
	}


}
