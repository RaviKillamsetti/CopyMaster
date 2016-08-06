package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import application.view.Item;

public class DataService {
	private static DataService dao = new DataService();
	private File dataFile;
	private JSONObject mainJson;
	private JSONArray itemsArr;
	// private JSONObject itemJson;
	private JSONParser parser = new JSONParser();
	private String itemKey = "items";
	private String passKey = "password";
	private List<Item> itemList;
	private Cipher cipher = null;
	private String ALGORITHM = "AES";
	private String key = "Zebis16digitPass";
	private Key secretKey;
	private String password;

	private String name = "name";
	private String value = "value";
	private String isPass = "isPass";

	private DataService() {
		dataFile = new File("data.encrypted");
		try {
			initCryptoFields();
			loadData();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		}
	}

	private void initCryptoFields() throws GeneralSecurityException {
		cipher = Cipher.getInstance(ALGORITHM);
		secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
	}

	public static DataService getDAO() {
		return dao;
	}

	private void loadData() throws IOException {

		if (!dataFile.exists()) {
			dataFile.createNewFile();
			mainJson = new JSONObject();
			itemsArr = new JSONArray();
			mainJson.put(itemKey, itemsArr);
			password = "";
			mainJson.put(passKey, password);
			persistMainJson();
		} else {
			try {
				byte[] encryptedBytes = readHex(dataFile);
				byte[] plainTextBytes = decrypt(encryptedBytes);
				String jsonStr = new String(plainTextBytes);
				mainJson = (JSONObject) parser.parse(jsonStr);
				itemsArr = (JSONArray) mainJson.get(itemKey);
				password = (String) mainJson.get(passKey);
			} catch (Exception e) {
				mainJson = new JSONObject();
				itemsArr = new JSONArray();
				e.printStackTrace();
			}
		}
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
		mainJson.put(passKey, password);
		mainJson.put(itemKey, itemsArr);
		persistMainJson();
	}

	public boolean containsKey(String key) {
		boolean contains = false;
		for (int i = 0; i < itemsArr.size(); i++) {
			JSONObject item = (JSONObject) itemsArr.get(i);
			if (item.get(name).equals(key)) {
				contains = true;
				break;
			}
		}
		return contains;
	}

	public void add(Item item) {

		if (containsKey(item.getName())) {
			JSONObject itemJson;
			for (int i = 0; i < itemsArr.size(); i++) {
				itemJson = (JSONObject) itemsArr.get(i);
				if (itemJson.get(name).equals(item.getName())) {
					itemJson.put(name, item.getName());
					itemJson.put(value, item.getValue());
					itemJson.put(isPass, item.isPassword());
					itemsArr.remove(i);
					itemsArr.add(i, itemJson);
					break;
				}
			}
			mainJson.put(itemKey, itemsArr);
			persistMainJson();
			initList();
		} else {
			JSONObject itemJson = new JSONObject();
			itemJson.put(name, item.getName());
			itemJson.put(value, item.getValue());
			itemJson.put(isPass, item.isPassword());
			itemsArr.add(itemJson);
			mainJson.put(itemKey, itemsArr);
			persistMainJson();
			itemList.add(item);
		}

	}

	public void remove(int index) {
		itemList.remove(index);
		itemsArr.clear();
		for (Item item : itemList) {
			JSONObject itemJson = new JSONObject();
			itemJson.put(name, item.getName());
			itemJson.put(value, item.getValue());
			itemJson.put(isPass, item.isPassword());
			itemsArr.add(itemJson);
		}
		mainJson.put(itemKey, itemsArr);
		initList();
		persistMainJson();
	}

	private void initList() {
		itemList.clear();
		int sNo = 1;
		for (int i = 0; i < itemsArr.size(); i++) {
			JSONObject itemJson = (JSONObject) itemsArr.get(i);
			itemList.add(new Item(sNo + "", (String) itemJson.get(name), (String) itemJson.get(value),
					(boolean) itemJson.get(isPass)));
			sNo++;
		}
	}

	public List<Item> getAllItems() {
		if (itemList == null) {
			itemList = new ArrayList<>();
		}
		initList();
		return itemList;
	}

	private void persistMainJson() {
		String jsonStr = mainJson.toJSONString();
		byte[] plainBytes = jsonStr.getBytes();
		try {
			byte[] encryptedBytes = encrypt(plainBytes);
			persistHEX(encryptedBytes);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void persistHEX(byte[] buffer) throws IOException {
		PrintWriter writer = new PrintWriter(dataFile);
		char[] c = Hex.encodeHex(buffer);
		String s = new String(c);
		writer.write(s);
		writer.close();
	}

	public byte[] readHex(File dataFile) {
		byte[] bArr = null;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(dataFile));
			String s = reader.readLine();
			char[] c = s.toCharArray();
			bArr = Hex.decodeHex(c);
			reader.close();
		} catch (IOException | DecoderException e) {
			e.printStackTrace();
		}
		return bArr;
	}

	private byte[] encrypt(byte[] plainTextByte) throws Exception {
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		byte[] encryptedBytes = cipher.doFinal(plainTextByte);
		return encryptedBytes;
	}

	private byte[] decrypt(byte[] encryptedBytes) throws Exception {
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
		return decryptedBytes;
	}
}
