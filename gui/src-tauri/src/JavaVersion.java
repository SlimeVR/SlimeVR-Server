public class JavaVersion {
	
	public static void main(String[] args)
	{
		var version = Runtime.version().version().get(0);
		System.exit(version);
	}
}
