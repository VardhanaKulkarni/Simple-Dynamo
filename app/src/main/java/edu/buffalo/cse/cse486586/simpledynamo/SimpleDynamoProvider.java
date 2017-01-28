package edu.buffalo.cse.cse486586.simpledynamo;

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.database.MatrixCursor;
import android.text.Selection;
import android.util.Log;
import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import android.os.AsyncTask;
import java.net.Socket;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Collections;


public class SimpleDynamoProvider extends ContentProvider {

	static final String TAG = SimpleDynamoProvider.class.getSimpleName();
	static final String[] REMOTE_PORT = {"11108","11112","11116","11120","11124"};
	static final int SERVER_PORT = 10000;
	static  String MYPort = "";
	static String succ_node = null;
	static  String prev_node = null;
	static String sec_succ_node = null;
	static String pre_prev_node = null;
	static String initNode = null;
	static Context context = null;
	static String retmsg = "";
	static Integer count = 1;
	static String FailedNode = null;
	static int deleteTag = 0;
	static int recoveryDone = 0;

	static HashMap<String,Integer> map = new HashMap<String, Integer>();
	static LinkedList<myObj> list1 = new LinkedList<myObj>();

	class myObj{
		String port;
		String Hashport;
		public myObj(String Port,String Hport){
			this.port = Port;
			this.Hashport = Hport;
		}
	}

	class compp implements Comparator<myObj> {
		@Override
		public int compare(myObj lhs, myObj rhs) {
			if (lhs.Hashport.compareTo(rhs.Hashport) < 0)
				return -1;
			if (rhs.Hashport.compareTo(lhs.Hashport) > 0)
				return 1;
			return 0;
		};
	}

	public void initHashMap(){
		for(int i=0;i<5;i++){
			map.put(REMOTE_PORT[i],1);
		}
	}


	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		try {

			for (int i = 0; i < 5; i++) {
				Socket socketd = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
						Integer.parseInt(REMOTE_PORT[i]));
				PrintWriter outd = new PrintWriter(socketd.getOutputStream());
				BufferedReader ind = new BufferedReader(new InputStreamReader(socketd.getInputStream()));
				outd.println("DELETEALL%");
				outd.flush();
				outd.close();
				socketd.close();
			}


			/*
			String Path = context.getFilesDir().getAbsolutePath();
			if (selection.equals("@") == false)
			selection = "*";
			//if(deleteTag == 0) {

				if (selection.equals("@")) {
                    //deleteTag =1;
					File file1 = new File(Path);
					File[] fileNames = file1.listFiles();
					Log.e("Filessssssss", Integer.toString(fileNames.length));

					for (int i = 0; i < fileNames.length; i++) {
						Log.e("from files directory", fileNames[i].getName());
						String FileN = fileNames[i].getName();

						File filex = new File(Path, FileN);
						filex.delete();
					}
				} else if (selection.equals("*")) {
					//deleteTag =1;
				/*if(prev_node!=null && succ_node != null) {
					Socket sockett = new Socket();
					sockett.connect(new InetSocketAddress(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
							Integer.parseInt(succ_node)));
					PrintWriter out = new PrintWriter(sockett.getOutputStream());
					BufferedReader in = new BufferedReader(new InputStreamReader(sockett.getInputStream()));
					String msgstar = "DELETESTAR" + "%" + MYPort;
					Log.e("VSK","Sending to next node");
					out.println(msgstar);
					out.flush();
					String Final = in.readLine();
					String localMsgs = deleteAllMsgs();
					Log.e("VSK","Got the final reply  "+ Final);
					// cursor = constructCursor(localMsgs + Final);
				}else{
					File file1 = new File(Path);
					File[] fileNames = file1.listFiles();
					Log.e("Filessssssss", Integer.toString(fileNames.length));

					for (int i = 0; i < fileNames.length; i++) {
						Log.e("from files directory", fileNames[i].getName());
						String FileN = fileNames[i].getName();

						File filex = new File(Path, FileN);
						Log.e("VVVVVVVVVV", "yayy found locally");
						BufferedReader readFile = new BufferedReader(new FileReader(filex));
						String value = readFile.readLine();
						readFile.close();
					}
				}*/
				/*	for (int i = 0; i < 5; i++) {
						Socket socketd = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
								Integer.parseInt(REMOTE_PORT[i]));
						PrintWriter outd = new PrintWriter(socketd.getOutputStream());
						BufferedReader ind = new BufferedReader(new InputStreamReader(socketd.getInputStream()));
						outd.println("DELETEALL%");
						outd.flush();
						//Log.e("deleteall", "waiting for the reply");
						//output[i] = ind.readLine();
						//if(output[i] == null){
						//	outd.close();
						//	socketd.close();
						//	continue;
						//}
						//Log.e("Starall reply "+ i,output[i]);
						outd.close();
						socketd.close();
					}


				} else {
					if (prev_node != null && succ_node != null) {
						String hashSelection = genHash(selection);
						String hashOwnnode = genHash(Integer.toString(Integer.parseInt(MYPort) / 2));
						String hashPrevnode = genHash(Integer.toString(Integer.parseInt(prev_node) / 2));


						if ((MYPort.equals((initNode)) && ((hashSelection.compareTo(hashOwnnode) <= 0) || (hashSelection.compareTo(hashPrevnode) > 0))) ||
								(!MYPort.equals((initNode)) && ((hashSelection.compareTo(hashOwnnode) <= 0) && (hashSelection.compareTo(hashPrevnode) > 0)))) {

						/*File file = new File(Path, selection);
						file.delete();

							File file1 = new File(Path);
							File[] fileNames = file1.listFiles();
							Log.e("Filessssssss", Integer.toString(fileNames.length));

							for (int i = 0; i < fileNames.length; i++) {
								Log.e("from files directory", fileNames[i].getName());
								String FileN = fileNames[i].getName();

								File filex = new File(Path, FileN);
								filex.delete();
							}
						} else {
							Socket socket = new Socket();
							socket.connect(new InetSocketAddress(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
									Integer.parseInt(succ_node)));
							PrintWriter out = new PrintWriter(socket.getOutputStream());
							BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
							out.println("DELETE" + "%" + selection + "%" + MYPort);
							Log.e("VVVVV", "Sending to next node");
							out.flush();
							Log.e("Query", "waiting to get " + selection);
							String Rmsg = in.readLine();
						}
					} else {
						File file = new File(Path, selection);
						file.delete();
					}
				}
			//}
		*/
		}catch(Exception e){
			Log.e(TAG,"EXCeption");
		}
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Uri insert(Uri uri, ContentValues values) {


		String name = values.getAsString("key");
		String msg = values.getAsString("value");
		Log.e("from insert", name + " " + msg);

		waitForRecovery();

		try {
			String hashedKey = genHash(name);
			Log.e("Hashed key", hashedKey);
			if(succ_node == null){
				Log.e("SUCC NODE is","NULL");
			}

			String hashOwnnode = genHash(Integer.toString(Integer.parseInt(MYPort) / 2));
			String hashPrevnode = genHash(Integer.toString(Integer.parseInt(prev_node)/ 2));
			Log.e("ports", MYPort + "  " + initNode);
			if(MYPort.equals(initNode)){
				Log.e("ports", "entered first node");
				if((hashedKey.compareTo(hashOwnnode)<=0) || (hashedKey.compareTo(hashPrevnode)>0)){
					Log.e("insert", "Storing Locally");

						storeContentProvider(name, msg,MYPort, context);


					int countreplicate = 1;
					Socket soccetReplicate = new Socket();
					soccetReplicate.connect(new InetSocketAddress(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
							Integer.parseInt(succ_node)));
					PrintWriter out = new PrintWriter(soccetReplicate.getOutputStream());
					BufferedReader in = new BufferedReader(new InputStreamReader(soccetReplicate.getInputStream()));
					out.println("REPLICATE"+"%"+name+"%"+msg+"%"+ "1"+"%"+MYPort);
					out.flush();
					Log.e("From insert","Waiting for the reply 1 "+ name);
					String life = in.readLine();
					if(life == null) {
						Log.e("From insert","got the reply for 1 " + name);
						FailedNode = succ_node;
						Log.e("Failure de", FailedNode);
						socketMsg("REPLICATE" + "%" + name + "%" + msg + "%" + "2" + "%" + MYPort, sec_succ_node);
						//out.println("REPLICATE"+"%"+name+"%"+msg+"%"+ "2"+"%"+MYPort);
					} else {
						Log.e("Insert", " insert successful " + name);
					}
					out.close();
					soccetReplicate.close();
				}else{
					Log.e("vardd", "sending to coordinator");
					String coodinator = findCoordinator(name);
					Log.e("The Coordinator", coodinator + " " + name);
					Socket socket = new Socket();
					socket.connect(new InetSocketAddress(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
							Integer.parseInt(coodinator)));
					PrintWriter out = new PrintWriter(socket.getOutputStream());
					BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					out.println("CHECK"+"%"+name+"%"+msg);
					out.flush();
					Log.e("From insert","Waiting for the reply 2 "+ name);
					String life = in.readLine();
					if(life == null) {
						Log.e("From insert","got the reply for 2 " + name);
						FailedNode = coodinator;
						Log.e("Failure de", FailedNode);
						handleFailure(coodinator,name,msg);
					} else {
						Log.e("Insert", " insert successful " + name);
					}
					out.close();
				}
			}else {
				Log.e("ports", "entered second node");
				if ((hashedKey.compareTo(hashOwnnode) <= 0) && (hashedKey.compareTo(hashPrevnode) > 0)) {

						storeContentProvider(name, msg,MYPort, context);

					Log.e("vardd", "Storing Locally");
					int countreplicate = 1;
					Socket socketReplicate = new Socket();
					socketReplicate.connect(new InetSocketAddress(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
							Integer.parseInt(succ_node)));
					PrintWriter out = new PrintWriter(socketReplicate.getOutputStream());
					BufferedReader in = new BufferedReader(new InputStreamReader(socketReplicate.getInputStream()));
					out.println("REPLICATE"+"%"+name+"%"+msg+"%"+ "1"+"%"+MYPort);
					out.flush();
					Log.e("From insert","Waiting for the reply 3 "+ name);
					String life = in.readLine();
					if(life == null) {
						Log.e("From insert","got the reply for 3 " + name);
						FailedNode = succ_node;
						Log.e("Failure de", FailedNode);
						socketMsg("REPLICATE" + "%" + name + "%" + msg + "%" + "2" + "%" + MYPort, sec_succ_node);
					} else {
						Log.e("Insert", " insert successful " + name);
					}

					out.close();
					socketReplicate.close();
				} else {
					Log.e("vardd", "sending to next node " );
					String coodinator = findCoordinator(name);
					Log.e("The Coordinator",coodinator+" "+name);
					Socket socket = new Socket();
					socket.connect(new InetSocketAddress(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
							Integer.parseInt(coodinator)));
					PrintWriter out = new PrintWriter(socket.getOutputStream());
					BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					out.println("CHECK" + "%" + name + "%" + msg);
					out.flush();
					Log.e("From insert","Waiting for the reply 4"+ name);
					String life = in.readLine();
					if(life == null) {
						Log.e("From insert","got the reply for 4" + name);
						FailedNode = coodinator;
						Log.e("Failure de", FailedNode);
						handleFailure(coodinator, name, msg);
					} else {
						Log.e("Insert", " insert successful " + name);
					}
					out.close();
				}
			}
			Log.e("SIMPLE", "-------------------------------------");

		}catch (NoSuchAlgorithmException e){
			Log.e(TAG,"Expection Generated");
		}catch(UnknownHostException e){
			Log.e(TAG,"UnknownHost Exception");
		}catch(IOException e){
			Log.e(TAG,"IO Exceptiion");
			StringWriter writer = new StringWriter();
			PrintWriter printWriter = new PrintWriter(writer);
			e.printStackTrace(printWriter);
			printWriter.flush();
			String t = writer.toString();
			Log.e("vardhana", t);
		}
		return uri;
	}

	public void socketMsg(String msg,String port){
		try {
			Socket socket = new Socket();
			socket.connect(new InetSocketAddress(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
					Integer.parseInt(port)));
			PrintWriter out = new PrintWriter(socket.getOutputStream());
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out.println(msg);
			Log.e("from client", "msg sent");
			out.flush();
			out.close();
		}catch(Exception e){
			Log.e("socketMsg", e.toString());
		}

	}

	public void handleFailure(String coordinator,String name,String msg){
		try {
			String next_cood = findNextCood(coordinator);
			Log.e("Recovery handle failure",next_cood + "  next cood");
			Socket soccetReplicate = new Socket();
			soccetReplicate.connect(new InetSocketAddress(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
					Integer.parseInt(next_cood)));
			PrintWriter out = new PrintWriter(soccetReplicate.getOutputStream());
			BufferedReader in = new BufferedReader(new InputStreamReader(soccetReplicate.getInputStream()));
			out.println("REPLICATE" + "%" + name + "%" + msg + "%" + "1" + "%" + coordinator);
			out.flush();
			Log.e("From insert handle", "waiting for reply 5 " + name + " " + coordinator);
			String reply = in.readLine();
			Log.e("From Inset 5", "got the reply 5 " + name + " " + reply);
		}catch(IOException e){
			Log.e("handle failure","exception");
		}
	}

	public String findNextCood(String coordinator){
		try {
			String hashCood = genHash(Integer.toString(Integer.valueOf(coordinator) / 2));
			myObj newobj = new myObj(coordinator,hashCood);

		//
		// 	int index = list1.indexOf(newobj);

			int index = 0;
			for(int i= 0;i<list1.size();i++) {
				if (coordinator.equals(list1.get(i).port)) {
					index = i;
					Log.e("index of the port is ", Integer.toString(index));
					if (index == list1.size() - 1) {
						return list1.get(0).port;
					} else {
						return list1.get(index + 1).port;
					}
				}
			}
		}catch (NoSuchAlgorithmException e){
			Log.e("No such algo","exception");
		}
	    return null;
	}

	public String findCoordinator(String key){
		String cood = "";
		try {
			String hashedKey = genHash(key);

			if((hashedKey.compareTo(list1.get(0).Hashport)) <=0 || hashedKey.compareTo(list1.get(4).Hashport) >0){
				cood = list1.get(0).port;
			}else if((hashedKey.compareTo(list1.get(1).Hashport)) <=0 && hashedKey.compareTo(list1.get(0).Hashport) >0) {
				cood = list1.get(1).port;
			}else if((hashedKey.compareTo(list1.get(2).Hashport)) <=0 && hashedKey.compareTo(list1.get(1).Hashport) >0) {
				cood = list1.get(2).port;
			}else if((hashedKey.compareTo(list1.get(3).Hashport)) <=0 && hashedKey.compareTo(list1.get(2).Hashport) >0) {
				cood = list1.get(3).port;
			}else if((hashedKey.compareTo(list1.get(4).Hashport)) <=0 && hashedKey.compareTo(list1.get(3).Hashport) >0) {
				cood = list1.get(4).port;
			}


		}catch(NoSuchAlgorithmException e){
			Log.e("from findCoordinator","exception");
		}
		return cood;
	}

	public String findLast(String key){
		String cood ="";
		try {
			String hashedKey = genHash(key);

			if((hashedKey.compareTo(list1.get(0).Hashport)) <=0 || hashedKey.compareTo(list1.get(4).Hashport) >0){
				cood = list1.get(2).port;
			}else if((hashedKey.compareTo(list1.get(1).Hashport)) <=0 && hashedKey.compareTo(list1.get(0).Hashport) >0) {
				cood = list1.get(3).port;
			}else if((hashedKey.compareTo(list1.get(2).Hashport)) <=0 && hashedKey.compareTo(list1.get(1).Hashport) >0) {
				cood = list1.get(4).port;
			}else if((hashedKey.compareTo(list1.get(3).Hashport)) <=0 && hashedKey.compareTo(list1.get(2).Hashport) >0) {
				cood = list1.get(0).port;
			}else if((hashedKey.compareTo(list1.get(4).Hashport)) <=0 && hashedKey.compareTo(list1.get(3).Hashport) >0) {
				cood = list1.get(1).port;
			}
		}catch(NoSuchAlgorithmException e){
			Log.e("from findCoordinator","exception");
		}
		return cood;

	}

	public String findLastMinusOne(String key){
		String cood ="";
		try {
			String hashedKey = genHash(key);
			if((hashedKey.compareTo(list1.get(0).Hashport)) <=0 || hashedKey.compareTo(list1.get(4).Hashport) >0){
				cood = list1.get(1).port;
			}else if((hashedKey.compareTo(list1.get(1).Hashport)) <=0 && hashedKey.compareTo(list1.get(0).Hashport) >0) {
				cood = list1.get(2).port;
			}else if((hashedKey.compareTo(list1.get(2).Hashport)) <=0 && hashedKey.compareTo(list1.get(1).Hashport) >0) {
				cood = list1.get(3).port;
			}else if((hashedKey.compareTo(list1.get(3).Hashport)) <=0 && hashedKey.compareTo(list1.get(2).Hashport) >0) {
				cood = list1.get(4).port;
			}else if((hashedKey.compareTo(list1.get(4).Hashport)) <=0 && hashedKey.compareTo(list1.get(3).Hashport) >0) {
				cood = list1.get(0).port;
			}
		}catch(NoSuchAlgorithmException e){
			Log.e("from findCoordinator","exception");
		}
		return cood;

	}


	public void storeContentProvider(String name,String msg,String Coordinator,Context context1){
		try {
			synchronized (this) {
				FileOutputStream fos;
				Log.e("from storep", name + "  " + msg);
				fos = context1.openFileOutput(name, context1.MODE_PRIVATE);
				if (fos == null) {
					Log.e("error", "fos is null");
				}
				String final_msg = msg + "%" + Coordinator;
				//String final_msg = msg;
				fos.write(final_msg.getBytes());
				fos.close();
			}
		} catch (IOException e) {
			Log.e(TAG, " IOException");
		}catch (Exception e){
			Log.e("TAG","Just some exception" +"   "+e.toString());
		}
	}

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		return false;
	}

	public String getAllMsgs()
	{
		Log.e("Geallmsg starall","getall");
		String Path = context.getFilesDir().getAbsolutePath();
		File file1 = new File(Path);
		File[] fileNames = file1.listFiles();
		Log.e("STARALL FILECOUNT ", Integer.toString(fileNames.length));
		String result = "";

		try {
			for (int i = 0; i < fileNames.length; i++) {
				Log.e("from files directory", fileNames[i].getName());
				String FileN = fileNames[i].getName();

				File filex = new File(Path, FileN);
				Log.e("VVVVVVVVVV", "yayy found locally");
				BufferedReader readFile = new BufferedReader(new FileReader(filex));
				String value = readFile.readLine();
				String final_value = value.split("%")[0];
				readFile.close();

				result += FileN + "-" + final_value + "%";
				Log.e("Output starall getall",result);
			}
		} catch (Exception e) {
			Log.e("exeption", e.toString());
		}
		Log.e("fresut getall ",result);
		return result;
	}

	public String getAllMissedMsgs(String port1,String port2,String port3)
	{
		Log.e("Recovery","Entered getAllMissedMsgs");
		if(context == null){
			Log.e("Recovery","Context is null");
		}
		String Path = context.getFilesDir().getAbsolutePath();

		File file1 = new File(Path);
		File[] fileNames = file1.listFiles();
		Log.e("Recovery fileslen", Integer.toString(fileNames.length));
		String result = "";

		try {
			for (int i = 0; i < fileNames.length; i++) {
				Log.e("Recovery","filename"+ fileNames[i].getName());
				String FileN = fileNames[i].getName();

				File filex = new File(Path, FileN);
				Log.e("VVVVVVVVVV", "yayy found locally");
				BufferedReader readFile = new BufferedReader(new FileReader(filex));
				String value = readFile.readLine();
				Log.e("Recovery value",value );
				String final_value = value.split("%")[0];
				String port_value = value.split("%")[1];
				Log.e("Recovery", "/s port " +FileN+" " +final_value+" "+ port_value + "==" + port1 + port2 + port3);
				readFile.close();
				if(value.split("%")[1].equals(port1) || value.split("%")[1].equals(port2) ||value.split("%")[1].equals(port3) ) {
					result += FileN + "-" + final_value + "-"+port_value+"%";
				}
			}
			if(!(result.length()==0)) {
				Log.e("sen missed messages",result);
				return result;
			}
		} catch (Exception e) {
			Log.e("exeption", e.toString());
		}
		return null;
	}



	public void deleteAllMsgs()
	{
		String Path = context.getFilesDir().getAbsolutePath();
		File file1 = new File(Path);
		File[] fileNames = file1.listFiles();
		Log.e("Filessssssss", Integer.toString(fileNames.length));
		String result = "";

		try {
			for (int i = 0; i < fileNames.length; i++) {
				Log.e("from files directory", fileNames[i].getName());
				String FileN = fileNames[i].getName();

				File filex = new File(Path, FileN);
				filex.delete();

			}
		} catch (Exception e) {
			Log.e("exeption", e.toString());
		}

	}



	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
						String sortOrder) {

		MatrixCursor cursor = new MatrixCursor(new String[]{"key","value"});
		String Path = context.getFilesDir().getAbsolutePath();

		waitForRecovery();

		Log.e("Query", selection + "key");
		Log.e("Content provider::::",Path);
		//synchronized (this) {
			try {
				if (selection.equals("@")) {

					File file1 = new File(Path);
					File[] fileNames = file1.listFiles();
					Log.e("Filessssssss", Integer.toString(fileNames.length));

					for (int i = 0; i < fileNames.length; i++) {
						Log.e("from files directory", fileNames[i].getName());
						String FileN = fileNames[i].getName();

						File filex = new File(Path, FileN);
						Log.e("VVVVVVVVVV", "yayy found locally");
						BufferedReader readFile = new BufferedReader(new FileReader(filex));
						String value = readFile.readLine();
						readFile.close();
						String final_value = value.split("%")[0];
						//String final_value = value;

						cursor.addRow(new Object[]{FileN, final_value});
					}
				} else if (selection.equals("*")) {
						/*Socket sockett = new Socket();
						sockett.connect(new InetSocketAddress(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
								Integer.parseInt(succ_node)));
						PrintWriter out = new PrintWriter(sockett.getOutputStream());
						BufferedReader in = new BufferedReader(new InputStreamReader(sockett.getInputStream()));
						String msgstar = "STAR" + "%" + MYPort;
						Log.e("VSK", "Sending to next node");
						out.println(msgstar);
						out.flush();
						String Final = in.readLine();
						String localMsgs = getAllMsgs();
						Log.e("VSK", "Got the final reply  " + Final);
						cursor = constructCursor(localMsgs + Final);*/
					String[] output = new String[5];
					Log.e("Entered Starall"+ MYPort,"entering loop");

					for(int i=0;i<5;i++){
						Socket socketv = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
								Integer.parseInt(REMOTE_PORT[i]));
						PrintWriter outv = new PrintWriter(socketv.getOutputStream());
						BufferedReader inv = new BufferedReader(new InputStreamReader(socketv.getInputStream()));
						outv.println("STARALL%");
						outv.flush();
						Log.e("starall","waiting for the reply");
						output[i] = inv.readLine();
						if(output[i] == null){
							outv.close();
							socketv.close();
							continue;
						}
						Log.e("Starall reply "+ i,output[i]);
						outv.close();
						socketv.close();
					}

					String final_output = "";
					for(int i=0;i<5;i++){
						if(output[i]!=null){
							final_output = final_output + output[i];
						}
						Log.e("temp final starall "+ i,final_output);
					}
					Log.e("starall final output",final_output);
					cursor = constructCursor(final_output);


				} else {
					//if (prev_node != null && succ_node != null) {
						String hashSelection = genHash(selection);
						String hashOwnnode = genHash(Integer.toString(Integer.parseInt(MYPort) / 2));
						String hashPrevnode = genHash(Integer.toString(Integer.parseInt(prev_node) / 2));


						//if ((MYPort.equals((initNode)) && ((hashSelection.compareTo(hashOwnnode) <= 0) || (hashSelection.compareTo(hashPrevnode) > 0))) ||
						//		(!MYPort.equals((initNode)) && ((hashSelection.compareTo(hashOwnnode) <= 0) && (hashSelection.compareTo(hashPrevnode) > 0)))) {


						//} else {
							String coordinator = findLast(selection);
							String Sendmessage = "QUERY" + "%" + selection + "%" + MYPort;
							String Rmsg = sendMessagetoDevice(Sendmessage,coordinator);
							boolean searchNext= false;
					Log.e("Query", "key1 " + selection + " *" + Rmsg + "* " + coordinator);
						if(Rmsg!=null && !(Rmsg!=null && Rmsg.equals("null"))) {
							Log.e("VVVVVV", "Final stage 1" + " " + Rmsg + " " + selection);
							cursor.addRow(new Object[]{selection, Rmsg});
						} else {
							coordinator = findLastMinusOne(selection);
							Rmsg = sendMessagetoDevice(Sendmessage,coordinator);
							if(Rmsg!=null && !(Rmsg!=null && Rmsg.equals("null"))) {
								Log.e("VVVVVV", "Final stage 2" + " " + Rmsg + " " + selection);
								cursor.addRow(new Object[]{selection, Rmsg});
							} else {
								Log.e("Query", "key2 " + selection + " " + Rmsg);
								coordinator = findCoordinator(selection);
								Rmsg = sendMessagetoDevice(selection, coordinator);
								Log.e("VVVVVV", "Final stage 3" + " " + Rmsg + " " + selection);
								cursor.addRow(new Object[]{selection, Rmsg});
							}
						}
							/*
							String[] RmsgArray;
							if(Rmsg!=null) {
								RmsgArray = Rmsg.split("%");
								if (RmsgArray[1].equals("null")){
									searchNext=true;
								} else {
									Log.e("VVVVVV", "Final stage 1" + " " + Rmsg);
									cursor.addRow(new Object[]{RmsgArray[0], RmsgArray[1]});
								}
							} else {
								searchNext = true;
							}
							if(searchNext==true){
								coordinator = findLastMinusOne(selection);
								Rmsg = sendMessagetoDevice(Sendmessage,coordinator);
								RmsgArray = Rmsg.split("%");
								Log.e("VVVVVV", "Final stage 2" + " " + Rmsg);
								cursor.addRow(new Object[]{RmsgArray[0], RmsgArray[1]});
							}
							*/
						//}
				 	/*} else {
						File file = new File(Path, selection);
						BufferedReader readFile = new BufferedReader(new FileReader(file));
						String value = readFile.readLine();
						String final_value = value.split("%")[0];
						readFile.close();
						//String final_value = value;
						cursor.addRow(new Object[]{selection, final_value});
					} */
				}
			} catch (Exception e) {
				Log.e(TAG, " IO Exception " + e.toString());
			}
		//}
		return cursor;
	}

	public String sendMessagetoDevice(String msg,String Port){
		try{
			Socket socket = new Socket();
			socket.connect(new InetSocketAddress(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
					Integer.parseInt(Port)));
			PrintWriter out = new PrintWriter(socket.getOutputStream());
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out.println(msg);
			out.flush();
			Log.e("Query", "waiting to get " + msg+" "+Port);
			String Rmsg = in.readLine();
			return Rmsg;

		}catch(Exception e){
			Log.e("sendMessageToDevice","Io "+e.toString() );
		}
		return null;
	}

	public MatrixCursor constructCursor(String Final){
		String[] FinaArray = Final.split("%");
		MatrixCursor cursor = new MatrixCursor(new String[]{"key","value"});
		Log.e("finalllllll",Integer.toString(FinaArray.length));
		for(int i=0;i< FinaArray.length;i++){
			String[] sub = FinaArray[i].split("-");
			String key = sub[0];
			String val = sub[1];
			cursor.addRow(new Object[]{key, val});
		}
		return cursor;
	}

	public String queryForward(Uri uurrii,String selection, String origPort){
		String Path = context.getFilesDir().getAbsolutePath();
		try {
			//String hashSelection = genHash(selection);
			//String hashOwnnode = genHash(Integer.toString(Integer.parseInt(MYPort) / 2));
			//String hashPrevnode = genHash(Integer.toString(Integer.parseInt(prev_node) / 2));

			//if ((MYPort.equals((initNode))&& ((hashSelection.compareTo(hashOwnnode) <= 0) || (hashSelection.compareTo(hashPrevnode) > 0))) ||
			//		(!MYPort.equals((initNode))&& ((hashSelection.compareTo(hashOwnnode) <= 0) && (hashSelection.compareTo(hashPrevnode) > 0))) ){
				waitForRecovery();

				Log.e("VVVV", "Found the msg in query forward  " + selection);
				File file = new File(Path, selection);
				BufferedReader readFile = new BufferedReader(new FileReader(file));
				String value = readFile.readLine();
				if (value == null || value.equals("null")) return value;

			    Log.e("Query Forward","Read "+ selection +" "+ value);
				String final_value = value.split("%")[0];

				//String final_value = value;
				readFile.close();
				return final_value;
			//}

		//}catch(NoSuchAlgorithmException e){
	//		Log.e("from QueryForward","No such algorithm");
		}catch (IOException e){
			Log.e(TAG,"Io");
		}
		return null;
	}

	public String deleteForward(Uri uurrii,String selection, String origPort){
		String Path = context.getFilesDir().getAbsolutePath();
		try {
			String hashSelection = genHash(selection);
			String hashOwnnode = genHash(Integer.toString(Integer.parseInt(MYPort) / 2));
			String hashPrevnode = genHash(Integer.toString(Integer.parseInt(prev_node) / 2));

			if ((MYPort.equals((initNode))&& ((hashSelection.compareTo(hashOwnnode) <= 0) || (hashSelection.compareTo(hashPrevnode) > 0))) ||
					(!MYPort.equals((initNode))&& ((hashSelection.compareTo(hashOwnnode) <= 0) && (hashSelection.compareTo(hashPrevnode) > 0))) ){
				Log.e("VVVV", "Found the msg in query forward  " + selection);
				File file = new File(Path, selection);
				file.delete();
				return "DELETE-COMPLETE";
			} else {
				Socket socket = new Socket();
				socket.connect(new InetSocketAddress(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
						Integer.parseInt(succ_node)));
				PrintWriter out = new PrintWriter(socket.getOutputStream());
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out.println("DELETE" + "%" + selection + "%" + origPort);
				out.flush();
				//out.close();
				Log.e("queryforward", "waiting " + selection);
				String Rmsg = in.readLine();
				return Rmsg;
			}
		}catch(NoSuchAlgorithmException e){
			Log.e("from QueryForward","No such algorithm");
		}catch (IOException e){
			Log.e(TAG,"Io");
		}
		return null;
	}

	public String queryForwardAll(String Origport){
		try {
			String Path = context.getFilesDir().getAbsolutePath();
			waitForRecovery();
			if (Origport.equals(succ_node)) {
				Log.e("VSK","Reached last node");
				File file1 = new File(Path);
				File[] fileNames = file1.listFiles();
				Log.e("Filessssssss", Integer.toString(fileNames.length));
				String toSend = "";

				for (int i = 0; i < fileNames.length; i++) {
					Log.e("from files directory", fileNames[i].getName());
					String FileN = fileNames[i].getName();

					File filex = new File(Path, FileN);
					Log.e("VVVVVVVVVV", "yayy found locally");
					BufferedReader readFile = new BufferedReader(new FileReader(filex));
					String value = readFile.readLine();
					String final_value = value.split("%")[0];
					readFile.close();
					Log.e("VSK", "sending back");
					toSend = toSend + FileN + "-" + final_value + "%";
				}
				return toSend;
			} else {
				String msgp = "STAR" + "%" + Origport;
				Socket sockett = new Socket();
				sockett.connect(new InetSocketAddress(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
						Integer.parseInt(succ_node)));
				PrintWriter out = new PrintWriter(sockett.getOutputStream());
				BufferedReader in = new BufferedReader(new InputStreamReader(sockett.getInputStream()));
				out.println(msgp);
				out.flush();
				Log.e("star ", "waiting to read");
				String msgreceived = in.readLine();

				Log.e("From successor", msgreceived);
				File file1 = new File(Path);
				File[] fileNames = file1.listFiles();
				Log.e("Filessssssss", Integer.toString(fileNames.length));
				//String toSend = "";

				for (int i = 0; i < fileNames.length; i++) {
					Log.e("from files directory *", fileNames[i].getName());
					String FileN = fileNames[i].getName();

					File filex = new File(Path, FileN);
					BufferedReader readFile = new BufferedReader(new FileReader(filex));
					String value = readFile.readLine();
					String final_value = value.split("%")[0];
					readFile.close();
					msgreceived = msgreceived + FileN + "-" + final_value + "%";
				}
				return msgreceived;
			}
		}catch (IOException e){
			Log.e(TAG,"IO excetion");
		}
		return  null;
	}



	public String deleteForwardAll(String Origport) {
		try {
			String Path = context.getFilesDir().getAbsolutePath();
			if (Origport.equals(succ_node)) {
				Log.e("VSK", "Reached last node");
				File file1 = new File(Path);
				File[] fileNames = file1.listFiles();
				Log.e("Filessssssss", Integer.toString(fileNames.length));
				String toSend = "";

				for (int i = 0; i < fileNames.length; i++) {
					Log.e("from files directory", fileNames[i].getName());
					String FileN = fileNames[i].getName();

					File filex = new File(Path, FileN);
					filex.delete();
					toSend = toSend;
					return toSend;
				}
			}else{
				String msgp = "DELETESTAR" + "%" + Origport;
				Socket sockett = new Socket();
				sockett.connect(new InetSocketAddress(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
						Integer.parseInt(succ_node)));
				PrintWriter out = new PrintWriter(sockett.getOutputStream());
				BufferedReader in = new BufferedReader(new InputStreamReader(sockett.getInputStream()));
				out.println(msgp);
				out.flush();
				Log.e("star ", "waiting to read");
				String msgreceived = in.readLine();

				Log.e("From successor", msgreceived);
				File file1 = new File(Path);
				File[] fileNames = file1.listFiles();
				Log.e("Filessssssss", Integer.toString(fileNames.length));
				//String toSend = "";

				for (int i = 0; i < fileNames.length; i++) {
					Log.e("from files directory *", fileNames[i].getName());
					String FileN = fileNames[i].getName();

					File filex = new File(Path, FileN);
					filex.delete();
					msgreceived = msgreceived;
				}
				return msgreceived;
			}
		}catch(IOException e){
			Log.e(TAG, "IO excetion");
		}
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	private String genHash(String input) throws NoSuchAlgorithmException {
		MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
		byte[] sha1Hash = sha1.digest(input.getBytes());
		Formatter formatter = new Formatter();
		for (byte b : sha1Hash) {
			formatter.format("%02x", b);
		}
		return formatter.toString();
	}

	public void socketCreate(String myport,Context contextt){
		try {
            /* Create a server socket as well as a thread (AsyncTask) that listens on the server
            * port.*/
			ServerSocket serverSocket = new ServerSocket(SERVER_PORT,100);
			new ServerTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, serverSocket);
			MYPort = myport;
			context = contextt;
		} catch (IOException e) {
			Log.e(TAG, "Can't create a ServerSocket");
			return;
		}
	}

	void waitForRecovery()
	{

		while(recoveryDone == 0)
		{
			try {
				Thread.sleep(10);
			}catch(Exception e){
				Log.e("waitfor recovery",e.toString());
			}
		}
	}


	private class ServerTask extends AsyncTask<ServerSocket, String, Void> {
		protected Void doInBackground(ServerSocket... sockets) {
			ServerSocket serverSocket = sockets[0];
			try {
				while (true){
					Socket Csocket = serverSocket.accept();
					Log.e("from server","connected" + MYPort);
					BufferedReader in = new BufferedReader(new InputStreamReader(Csocket.getInputStream()));
					PrintWriter out = new PrintWriter(Csocket.getOutputStream());
					String msgReceived ="";
					String unOrderedList = "";
					String ordderedList = "";
					LinkedList<myObj> list =new LinkedList<myObj>();

					msgReceived = in.readLine();
					waitForRecovery();
					Log.e("Server main msgg var", msgReceived);

					if (msgReceived == null) continue;

					String[] msgR = msgReceived.split("%");

					if(msgR[0].equals("CHECK")){
						String value1 = msgR[1];
						String value2 = msgR[2];
						Log.e("Recover Check","entered check block");
						//out.println("ALIVE");
						//out.flush();
						//out.close();
						Log.e("vardd","recached forwarded node **********************"+value1+value2);

						ContentValues values = new ContentValues();
						values.put("key", value1);
						values.put("value", value2);
						Uri uurrii = buildUri("content", "edu.buffalo.cse.cse486586.simpledynamo.provider");
						Thread thObj = new Thread(new CallInsertTh(values,uurrii,Csocket));
						thObj.start();
						//insert(uurrii,values);
					}

					if(msgR[0].equals("QUERY")){
						Log.e("VVVVV","Received Query request");
						String selec = msgR[1];
						// Integer tcount = Integer.valueOf(msgR[2]);
						Uri uurrii = buildUri("content", "edu.buffalo.cse.cse486586.simpledynamo.provider");
						Log.e("Server query ", "forwading " + selec);
						String reply = queryForward(uurrii, selec,msgR[2]);
						Log.e("VVVVV","Got a reply" +"  "+reply);
						Log.e("VVVVV", "sending " + "**" + selec + "%" + reply + "%" + msgR[2]);
						//out.println(selec+"%"+reply+"%"+msgR[2]);
						out.println(reply);
						out.flush();
						out.close();
					}
					if(msgR[0].equals("STAR")){
						String origPort = msgR[1];
						String res = queryForwardAll(origPort);
						String[] Resarray = res.split("%");
						Log.e("VSK","Got he reply :  "+ res+" "+Resarray.length);
						out.println(res);
						out.flush();
					}

					if(msgR[0].equals("DELETESTAR")){
						String origPort = msgR[1];
						String res = queryForwardAll(origPort);
						out.println(res);
						out.flush();
					}

					if(msgR[0].equals("DELETEALL")){
						Uri uurrii = buildUri("content", "edu.buffalo.cse.cse486586.simpledynamo.provider");
						deleteAllMsgs();
					}

					if(msgR[0].equals("DELETE")){
						Log.e("VVVVV","Received Query request");
						String selec = msgR[1];
						// Integer tcount = Integer.valueOf(msgR[2]);
						Uri uurrii = buildUri("content", "edu.buffalo.cse.cse486586.simpledynamo.provider");
						Log.e("DELETE  ", "forwading " + selec);
						String reply = deleteForward(uurrii, selec, msgR[2]);
						Log.e("VVVVV","Got a reply DELETE" +"  "+reply);
						Log.e("VVVVV","sending "+ "**"+selec+"%"+reply+"%"+msgR[2]);
						out.println(reply);
						out.flush();
						out.close();
					}

					if(msgR[0].equals("REPLICATE")) {
						String key = msgR[1];
						String value = msgR[2];
						String count = msgR[3];
						String Cood = msgR[4];

						Log.e("Replicate", key + " " + value + " " + count);

						if (Integer.valueOf(count) == 2) {

								storeContentProvider(key, value,Cood, context);
								out.println("ALIVE");
								out.flush();
								Log.e("REPLICATE", "Insertion successful " + key);
							    //out.close();

						}else if(Integer.valueOf(count)==1){

								storeContentProvider(key, value, Cood, context);
								//out.println("ALIVE");
								//out.flush();
								//out.close();
							    String msgT0oSend = "REPLICATE" + "%" + key + "%" + value + "%" + "2" + "%" + Cood;
							    Thread thObj = new Thread(new replicateTh(msgT0oSend,succ_node,Csocket));
							    thObj.start();
                        /*
							Socket socket = new Socket();
							socket.connect(new InetSocketAddress(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
									Integer.parseInt(succ_node)));
							PrintWriter outt1 = new PrintWriter(socket.getOutputStream());
							BufferedReader inn1 = new BufferedReader(new InputStreamReader(socket.getInputStream()));
							outt1.println("REPLICATE" + "%" + key + "%" + value + "%" + "2" + "%" + Cood);
							Log.e("from repli", "count is 2, sending to next node " + key + " " + succ_node);
							outt1.flush();
							String life = inn1.readLine();
							if(life == null) {
								FailedNode = succ_node;
								Log.e("Failure de", FailedNode);
							}

								//outt1.close();
							*/
						}

					}
					if(msgR[0].equals("DELETEREPLICA")){
						String selection = msgR[1];
						String count = msgR[2];
						if(Integer.valueOf(count) ==1){
							deletealll(selection);
							Socket socket = new Socket();
							socket.connect(new InetSocketAddress(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
									Integer.parseInt(succ_node)));
							PrintWriter outt1 = new PrintWriter(socket.getOutputStream());
							BufferedReader inn1 = new BufferedReader(new InputStreamReader(socket.getInputStream()));
							outt1.println("DELETEREPLICA" + "%" + selection + "%"+"2");
							Log.e("from repli","count is 2");
							outt1.flush();
							//outt1.close();
						}else if(Integer.valueOf(count) == 2){
							deletealll(selection);
						}
					}

					if(msgR[0].equals("ALIVE")){
						Log.e("Recovery","Got the alive msg");
						out.println("Yes I am alive");
						out.flush();
					}

					if(msgR[0].equals("RECOVER")){
						Log.e("Recovery","Entered recovery in server");
						String port1 = msgR[1];
						String port2 = msgR[2];
						String port3 = msgR[3];
						String missmsg = getAllMissedMsgs(port1,port2,port3);
						if(missmsg == null){
							Log.e("Recovery","missmsg is null");
							out.println("NO");
							out.flush();
						}else{
							Log.e("Recovery","READ" +  missmsg);
							out.println(missmsg);
							out.flush();
						}
					}

					if(msgR[0].equals("STARALL")){
						Log.e("Entered server","erfcd");
						String reply = getAllMsgs();
						Log.e("Got the reply ",reply);
						out.println(reply);
						out.flush();
					}

					/*if(msgR[0].equals("SAVE")){
						String[] toSavemsg = msgR[1].split("%");
						Log.e("SAVE","entered");
						for(int i=0;i<toSavemsg.length;i++){
							Log.e("Saving the message ",toSavemsg[i]+ " ");
							String[] temp = toSavemsg[i].split("-");
							String name = temp[0];
							String value = temp[1];
							String portt = temp[2];
							storeContentProvider(name,value,portt,context);
						}
					}*/
				}
			} catch (IOException e) {
				Log.e(TAG, "serverTask socket IOException"+e.toString());
			}
			return null;
		}
	}

	class CallInsertTh implements Runnable{
		ContentValues cv;
		Uri uri;
		Socket callerSocket;
		public CallInsertTh(ContentValues cv,Uri uri, Socket s){
			this.cv = cv;
			this.uri = uri;
			this.callerSocket = s;
		}
		public void run(){
			insert(uri,cv);
			try {
				PrintWriter out = new PrintWriter(callerSocket.getOutputStream());
				out.println("ALIVE");
				out.flush();
			} catch (Exception e) {
				Log.e("CallInsertTh", e.toString());
			}
		}
	}

	class replicateTh implements Runnable{
		Socket callerSocket;
		String msg, succ_port;
		public replicateTh(String msg, String succ_port, Socket socket){
			this.msg = msg;
			this.succ_port = succ_port;
			this.callerSocket = socket;
		}
		public void run(){
         try {
			 Socket socket = new Socket();
			 socket.connect(new InetSocketAddress(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
					 Integer.parseInt(succ_port)));
			 PrintWriter outt1 = new PrintWriter(socket.getOutputStream());
			 BufferedReader inn1 = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			 outt1.println(msg);
			 //Log.e("from repli", "count is 2, sending to next node " + key + " " + succ_node);
			 outt1.flush();
			 String life = inn1.readLine();
			 if (life == null) {
				 FailedNode = succ_node;
				 Log.e("Failure de", FailedNode);
			 }

			 PrintWriter out = new PrintWriter(callerSocket.getOutputStream());
			 BufferedReader in = new BufferedReader(new InputStreamReader(callerSocket.getInputStream()));
			 out.println("ALIVE");
			 out.flush();
		 }catch(IOException e){
			 Log.e("IO excption","IO");
		 }
		}
	}
	private  void deleteReplica(String selection){
		String Path = context.getFilesDir().getAbsolutePath();
		File file = new File(Path, selection);
		file.delete();
	}

	private void deletealll(String selection){
		String Path = context.getFilesDir().getAbsolutePath();
		File file1 = new File(Path);
		File[] fileNames = file1.listFiles();
		Log.e("Filessssssss", Integer.toString(fileNames.length));

		for (int i = 0; i < fileNames.length; i++) {

			String FileN = fileNames[i].getName();

			File filex = new File(Path, FileN);
			filex.delete();
		}
	}

	public String setChainOrder(){
		try {

			for (int i = 0; i < 5; i++) {
				String Hport = genHash(Integer.toString(Integer.parseInt(REMOTE_PORT[i]) / 2));
				list1.add(new myObj(REMOTE_PORT[i], Hport));
			}
			Collections.sort(list1, new compp());

			String sendString = "ORDER";

			Iterator<myObj> it = list1.iterator();
			while (it.hasNext()) {
				myObj cur = it.next();
				Log.e("From Join", cur.port);
				sendString = sendString + "%" + cur.port;
			}

			Log.e("From jo***********", sendString);

			for(int l=0;l<list1.size();l++){
				if(MYPort.equals(list1.get(l).port)){
					if(l==0){
						prev_node = list1.getLast().port;
						succ_node = list1.get(l + 1).port;
						sec_succ_node = list1.get(l + 2).port;
						pre_prev_node = list1.get(list1.size()-2).port;
					}else if(l == (list1.size()-1)){
						prev_node = list1.get(l-1).port;
						succ_node =list1.get(0).port;
						sec_succ_node = list1.get(1).port;
						pre_prev_node = list1.get(l-2).port;
					}else if(l==(list1.size()-3)){
						prev_node = list1.get(l-1).port;
						succ_node = list1.get(l+1).port;
						pre_prev_node = list1.get(l-2).port;
						sec_succ_node = list1.get(l+2).port;
					}else if(l ==(list1.size()-2)){
						prev_node = list1.get(l-1).port;
						succ_node = list1.get(l+1).port;
						pre_prev_node = list1.get(l-2).port;
						sec_succ_node = list1.get(0).port;
					}else if(l== (list1.size()-4)){
						prev_node = list1.get(l-1).port;
						succ_node = list1.get(l+1).port;
						sec_succ_node = list1.get(l+2).port;
						pre_prev_node = list1.getLast().port;
					}
				}
				initNode = list1.get(0).port;
			}
			Log.e(" first successor is",succ_node);
			Log.e(" first predessor ",prev_node);
			Log.e("first sec successo is",sec_succ_node);
			Log.e(" first sec predesso ",pre_prev_node);
			Log.e("first myport",MYPort);
//
			Log.e("INIT", initNode);
			return(succ_node+"%"+prev_node +"%"+sec_succ_node +"%"+pre_prev_node);

		}catch (NoSuchAlgorithmException e){
			Log.e("From chainorder","exception");
		}
		return null;
	}

	private Uri buildUri(String scheme, String authority) {
		Uri.Builder uriBuilder = new Uri.Builder();
		uriBuilder.authority(authority);
		uriBuilder.scheme(scheme);
		return uriBuilder.build();
	}

	public void setRecovery(){
		recoveryDone = 1;
	}


}