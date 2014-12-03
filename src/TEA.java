
public class TEA {
	
	public TEA () {
		System.loadLibrary("TEA");
	}
	
	public String getEncrypted(long[] value, long[] key){
		long v[] = getEncryptedRaw(value, key);
		return String.valueOf(v[0]) + "\t" + String.valueOf(v[1]);
	}
	
	public long[] getEncryptedRaw(long[] value, long[] key){
		return encrypt(value, key);
	}
	
	public long[] getDecrypted(String value, long[] key){
		long[] v = new long[2];
		v[0] = Long.parseLong(value.split("\\t")[0]);
		v[1] = Long.parseLong(value.split("\\t")[1]);
		return decrypt(v, key);
	}
	
	public long[] getDecryptedRaw(long[] value, long[] key){
		return decrypt(value, key);
	}
	
	private native long[] encrypt(long[] value, long[] key);
	private native long[] decrypt(long[] value, long[] key);
}
