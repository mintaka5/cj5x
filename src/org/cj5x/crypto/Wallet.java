package org.cj5x.crypto;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.Date;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.bitcoinj.core.Base58;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class Wallet {
	private PrivateKey privateKey;
	private PublicKey publicKey;
	private String secret;
	private String key;
	private long created;
	private double balance;
	
	public Wallet() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, UnsupportedEncodingException {
		Security.addProvider(new BouncyCastleProvider());
		
		setCreated(Instant.now().getEpochSecond());
		
		this.makeKeys();
	}

	private void makeKeys() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, UnsupportedEncodingException {
		KeyPairGenerator generator = KeyPairGenerator.getInstance("EC", "BC");
		
		ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256k1");
		generator.initialize(ecSpec);
		
		KeyPair pair = generator.generateKeyPair();
		
		setPrivateKey(pair.getPrivate());
		setPublicKey(pair.getPublic());
		
		createSecret();
		
		createKey();
	}
	
	/**
	 * PEM stuff
	 * @throws IOException 
	 */
	public String toPem() throws IOException {
		StringWriter sw = new StringWriter();
		PemWriter pemw = new PemWriter(sw);
		pemw.writeObject(new PemObject("PRIVATE RSA KEY", this.getPrivateKey().getEncoded()));
		pemw.flush();
		pemw.close();
				
		return sw.toString();
	}
	
	public void fromPem(String f) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
		PemReader reader = new PemReader(new FileReader(f));
		PemObject pem = reader.readPemObject();
		byte[] content = pem.getContent();
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(content);
		
		// generate private key from pem file
		KeyFactory kf = KeyFactory.getInstance("ECDSA", "BC");
		PrivateKey pk = kf.generatePrivate(spec);
		setPrivateKey(pk);
		
		// TODO generate pub key off this private key
		
		
		System.out.println("Key: " + Base64.encodeBase64String(pk.getEncoded()));
		
		reader.close();
	}

	/**
	 * Generate public key for wallet sharing
	 * @throws NoSuchAlgorithmException 
	 * @throws UnsupportedEncodingException 
	 * @throws NoSuchProviderException 
	 */
	private void createKey() throws NoSuchAlgorithmException, UnsupportedEncodingException, NoSuchProviderException {
		ECPublicKey key = (ECPublicKey) this.getPublicKey();
		ECPoint point = key.getW();
		
		String sX = adjustTo64(point.getAffineX().toString(16));
		String sY = adjustTo64(point.getAffineY().toString(16));
		String bcPub = "cj5x" + sX + sY;
		
		MessageDigest sha = MessageDigest.getInstance("SHA-256");
		byte[] s1 = sha.digest(bcPub.getBytes("UTF-8"));
		
		MessageDigest rmd160 = MessageDigest.getInstance("RipeMD160", "BC");
		byte[] r1 = rmd160.digest(s1);
		
		// add 0x00 version byte at the beginning of hash
		byte[] r2 = new byte[r1.length+1];
		r2[0] = 0;
		for(int i=0; i<r1.length; i++) r2[i+1] = r1[i];
		
		// repeat sha-256 hash twice
		byte[] s2 = sha.digest(r2);
		byte[] s3 = sha.digest(s2);
		
		// first 4 bytes of the second hash are used as address checksum and attached to RipeMD160 hash
		byte[] a1 = new byte[25];
		for(int i=0; i<r2.length; i++) a1[i] = r2[i];
		for(int i=0; i<5; i++) a1[20+i] = s3[i];
		
		String b58 = Base58.encode(a1);
		
		this.setKey(b58);
	}

	/**
	 * Store private key string for wallet
	 */
	private void createSecret() {
		ECPrivateKey p = (ECPrivateKey) this.getPrivateKey();
		
		this.setSecret(adjustTo64(p.getS().toString(16)));
	}

	/**
	 * Create a 64 char string padded with 0s if needed
	 * @param String s
	 * @return String
	 */
	private String adjustTo64(String s) {
		int l = s.length();
		
		if(l < 62 || l > 64) {
			throw new IllegalArgumentException("Not a valid key: " + s);
		}
		
		return StringUtils.leftPad(s, 64, "0");
	}
	
	public String bytesToHex(byte[] b) {
	    return new BigInteger(1, b).toString(16);
	}

	public PrivateKey getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(PrivateKey privateKey) {
		this.privateKey = privateKey;
	}

	public PublicKey getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(PublicKey publicKey) {
		this.publicKey = publicKey;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public long getCreated() {
		return created;
	}

	public void setCreated(long created) {
		this.created = created;
	}

	@Override
	public String toString() {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.setPrettyPrinting();
		gsonBuilder.disableHtmlEscaping();
		
		JsonSerializer<Wallet> serializer = new JsonSerializer<Wallet>() {
			@Override
			public JsonElement serialize(Wallet src, Type typeOfSrc, JsonSerializationContext context) {
				JsonObject json = new JsonObject();
				
				Instant instant = Instant.ofEpochSecond(src.getCreated());
				Date date = Date.from(instant);
				DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
				String created = fmt.print(date.getTime());
				
				JsonObject keyJson = new JsonObject();
				//keyJson.addProperty("private", encoder.encodeToString(src.getPrivateKey().getEncoded()));
				keyJson.addProperty("public", Base64.encodeBase64String(src.getPublicKey().getEncoded()));
				
				json.addProperty("created", created);
				json.addProperty("address", src.getKey());
				json.add("keys", keyJson);
				
				return json;
			}
		};
		
		gsonBuilder.registerTypeAdapter(Wallet.class, serializer);
		
		Gson gson = gsonBuilder.create();
		
		return gson.toJson(this);
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}
}
