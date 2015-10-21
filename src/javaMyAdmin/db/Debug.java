package javaMyAdmin.db;

public class Debug {
	public static String check(String url){
		if(url.equalsIgnoreCase("debug")){
			Debug.run();
			url = "localhost";
		}
		return url;
	}
	public static void run(){
		System.out.println("run Debug!");
	}
}
