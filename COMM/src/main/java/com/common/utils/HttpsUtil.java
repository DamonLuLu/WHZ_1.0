package com.common.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSON;

/**
 * @author sailor
 * @comment https无证书调用api工具类
 * @corp 星移软件
 */
public class HttpsUtil {

	// static Log logger = LogFactory.getLog(HttpsUtil.class);
	/**
	 * 忽视证书HostName
	 */
	private static HostnameVerifier ignoreHostnameVerifier = new HostnameVerifier() {
		public boolean verify(String s, SSLSession sslsession) {
			return true;
		}
	};
	/**
	 * Ignore Certification
	 */
	private static TrustManager ignoreCertificationTrustManger = new X509TrustManager() {

		private X509Certificate[] certificates;

		@Override
		public void checkClientTrusted(X509Certificate certificates[],
				String authType) throws CertificateException {
			if (this.certificates == null) {
				this.certificates = certificates;
			}
		}

		@Override
		public void checkServerTrusted(X509Certificate[] ax509certificate,
				String s) throws CertificateException {
			if (this.certificates == null) {
				this.certificates = ax509certificate;
			}

		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			// TODO Auto-generated method stub
			return null;
		}
	};

	/**
	 * 调用https api 获取报文
	 * 
	 * @param urlString
	 * @return
	 */
	public static String doGet(String url) {
		StringBuffer response = new StringBuffer();
		try {
			HttpClient client = new HttpClient();
			GetMethod method = new GetMethod(url);
			method.addRequestHeader("Content-Type", "application/json;charset="
					+ "UTF-8");

			int status = client.executeMethod(method);
			System.out.println("code:" + status);
			if (status == HttpStatus.SC_OK) {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(method.getResponseBodyAsStream(),
								"UTF-8"));
				String line;
				while ((line = reader.readLine()) != null) {
					response.append(line);
				}
				reader.close();
			}
		} catch (Exception ex) {
			//logger.error(null, ex);
		} finally {
		}
		return response.toString();
	}

	/**
	 * 执行一个HTTP POST请求，返回请求响应的数据
	 * 
	 * @param url
	 *            请求的URL地址
	 * @param params
	 *            请求的查询参数,可以为null
	 * @param charset
	 *            字符集
	 * @param pretty
	 *            是否美化
	 * @return 返回请求响应的HTML
	 */
	public static String doPost(String url, Map<String, String> params,
			String charset, boolean pretty) {
		StringBuffer response = new StringBuffer();
		HttpClient client = new HttpClient();
		HttpMethod method = new PostMethod(url);
		// 设置Http Post数据
		// 本地测试中发现此方式可能会出现传参无法成功，暂时不改，若出现Bug,替代方案详见ESunAction中的doPost方法
		if (params != null) {
			HttpMethodParams p = new HttpMethodParams();
			for (Map.Entry<String, String> entry : params.entrySet()) {
				System.err.println("key: " + entry.getKey() + " val: "
						+ entry.getValue());
				p.setParameter(entry.getKey(), entry.getValue());
			}
			method.setParams(p);

		}
		try {
			client.executeMethod(method);
			if (method.getStatusCode() == HttpStatus.SC_OK) {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(method.getResponseBodyAsStream(),
								charset));
				String line;
				while ((line = reader.readLine()) != null) {
					if (pretty)
						response.append(line).append(
								System.getProperty("line.separator"));
					else
						response.append(line);
				}
				reader.close();
			}
		} catch (IOException e) {
			//logger.error("执行HTTP Post请求" + url + "时，发生异常！", e);
		} finally {
			method.releaseConnection();
		}
		return response.toString();
	}

	/**
	 * post请求
	 * 
	 * @param url
	 * @param params
	 * @return
	 * @throws IOException
	 */
	public static String doPost(String url, String params, String charSet) {
		// Post请求的url
		try {
			URL postUrl = new URL(url);
			// 打开连接
			HttpURLConnection connection = (HttpURLConnection) postUrl
					.openConnection();
			// 设置是否向connection输出，因为这个是post请求，参数要放在
			// http正文内，因此需要设为true
			connection.setDoOutput(true);
			// Read from the connection. Default is true.
			connection.setDoInput(true);
			// Set the post method. Default is GET
			connection.setRequestMethod("POST");
			// Post 请求不能使用缓存
			connection.setUseCaches(false);
			// URLConnection.setInstanceFollowRedirects是成员函数，仅作用于当前函数
			connection.setInstanceFollowRedirects(true);
			// 配置本次连接的Content-type，配置为application/x-www-form-urlencoded的
			// 意思是正文是urlencoded编码过的form参数，下面我们可以看到我们对正文内容使用URLEncoder.encode
			// 进行编码
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			// String content = URLEncoder.encode(params, "utf-8");
			// 连接，从postUrl.openConnection()至此的配置必须要在connect之前完成，
			// 要注意的是connection.getOutputStream会隐含的进行connect。
			connection.connect();
			DataOutputStream out = new DataOutputStream(
					connection.getOutputStream());
			// DataOutputStream.writeBytes将字符串中的16位的unicode字符以8位的字符形式写道流里面
			out.write(params.getBytes());
			// out.writeUTF(params);
			out.flush();
			out.close(); // flush and close
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream(), charSet));// 设置编码,否则中文乱码
			String line = "";
			StringBuffer result = new StringBuffer();
			while ((line = reader.readLine()) != null) {
				result.append(line);
			}
			reader.close();
			connection.disconnect();
			return result.toString();
		} catch (Exception e) {
			//logger.error(null, e);
		}
		return null;
	}

	/**
	 * post请求
	 * 
	 * @param url
	 * @param params
	 * @return
	 * @throws IOException
	 */
	public static String doPost(String url, String params) {
		StringBuffer response = new StringBuffer();
		try {
			HttpClient client = new HttpClient();
			PostMethod post = new PostMethod(url);
			post.setRequestEntity(new StringRequestEntity(params,
					"application/json", "UTF-8"));

			int status = client.executeMethod(post);
			System.out.println("code:" + status);
			if (status == HttpStatus.SC_OK) {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(post.getResponseBodyAsStream(),
								"UTF-8"));
				String line;
				while ((line = reader.readLine()) != null) {
					response.append(line);
				}
				reader.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			//logger.error(null, ex);
		} finally {
		}
		return response.toString();
	}

	/* 发送Post请求 */
	public static String doPost(String url, Object body) {
		// post请求
		String par = JSON.toJSONString(body);
		return doPost(url, par);
	}

	/* 发送Post请求 */
	public static String doPost(String url, Object body,
			Map<String, Object> params) {
		// post请求
		String par = JSON.toJSONString(params);
		if (params == null) {
			return doPost(url, par);
		}
		url = getCombiUrl(url, params);
		return doPost(url, par);
	}

	/* 发送Get请求 */
	public static String doGet(String url, Map<String, Object> params) {
		if (params == null) {
			return doGet(url);
		}

		return doGet(getCombiUrl(url, params));
	}

	// 组合地址
	private static String getCombiUrl(String url, Map<String, Object> params) {
		// post请求
		StringBuilder sb = new StringBuilder();
		int index = 0;
		if (null != params) {
			for (Entry<String, Object> entry : params.entrySet()) {
				String key = entry.getKey();
				try {
					String value = entry.getValue().toString();
					if (index == 0) {
						sb.append(key + "=" + value);
					} else {
						sb.append("&" + key + "=" + value);
					}
				} catch (Exception e) {
					//logger.error(null, e);
				}
				index++;
			}
		}
		return url + "?" + sb.toString();
	}

	public static void main(String[] args) {
		int a = 0;
		String[] arry = new String[] { "oEgRlt-IZxdpvrXI15vGmxgFkxpY",
				"oEgRlt3cuodVIODaSVDP6cWAck9Q", "oEgRlt0LhMMQhGlKedcn0Wk_xBsw",
				"oEgRlt5etl1OXWk4yiELEds_Y36A", "oEgRltxDsI5XQ4iykQwHeXJHwIm0",
				"oEgRlt0Qqs9LKIxyCEfirNNyT0JM", "oEgRlt5BOizZJ7pV3_WyUaZZoWXU",
				"oEgRltwM_IQ7uzqOdgmaRUPgLKxc", "oEgRlt2Z0s_iJi7wnG1HQj16iCf0",
				"oEgRltxYAJPWhs9T-5KPwcBZg9Fk", "oEgRltx-EKoXsD53Vwrdtuxf4jok",
				"oEgRlt-q97Gst5ZfGFevOfhCVJu8", "oEgRlt78y-psUI8p3g_9uXBlit1s",
				"oEgRltytQ-2PHPUu16Z4GF3GSdFg", "oEgRlt9rvWXJw6jPBMs03ltHSpDI",
				"oEgRlt8BUXOHxHDQ77FE1LxgqThU", "oEgRlt_1LvvGekGHOtUH12dZIa2c",
				"oEgRlt3vsKUvJWjHyDdk8dQUo_Yg", "oEgRltwsv85UYCumHjDhWn37dXJY",
				"oEgRlt9RX4NN6SwAcF9PnsHYJN_o", "oEgRltyttYKuboFlzHK7NATcXwhc",
				"oEgRltx6_RukiXkCRaRMh4yakrno", "oEgRlt2IzpV-dXFezjyfGxcPVOFU",
				"oEgRlt6Q5Sa_itjOH8ZqwByfuiSg", "oEgRlt6QbV-Wj4RJwAU3PoyhxklY",
				"oEgRlt1_CO6u8z8hSFcxO-q5X078", "oEgRlt_QJkU2_6TzA-HK_U1XHnXI",
				"oEgRlt6cygESlbmnk1iaWBzTWuGw", "oEgRltxCK9cANf50QSY8HfzE8BdI",
				"oEgRlt82mGchIZVstWLySoL3LT1E", "oEgRltxkrzAOX2hStx20Biwef3BI",
				"oEgRlt8i3mhV1ITu9PhcVo7pE8Es", "oEgRlt0gWiy2Y-bbUteh8cN6dFLw",
				"oEgRlt-Iu4rFjZgMYgeHS0YKTxTg", "oEgRlt0KX_acLVNMqpTcPiNRL7Y8",
				"oEgRlt38Bs0Ny5QGc1AmaV5r6hlo", "oEgRltzdRV3Xlf4UF_TVXpdw2Q3g",
				"oEgRlt85lwPBfQhPW-rLjI1FYaks", "oEgRlt9t_XEIX9rHIAmornb7tnss",
				"oEgRlt-KhX-8niQHPm6eo1HG2Clk", "oEgRlt5sozE61Z-BBB0mYlxFgaO0",
				"oEgRlt1CHuuwh2LpZIdqoCLqTn-M", "oEgRlt-5GdKE50NUM_eRFZ7Pao78",
				"oEgRltwNQRUhW2zZkwes02ZEQw4A", "oEgRlt8XYja0qOC6Qq1xoOxWuGBQ",
				"oEgRlt2_U5z8ETMqae1JDi1PlKRE", "oEgRltxM6c40G8PO08DQhiUTdPVg",
				"oEgRltxwu4-GcVIm85t3Ie5NN0Uk", "oEgRlt7RB85keoAxKX3qC60JsoOk",
				"oEgRlt3ByVx_3oIvvrYnHqVgEyz8", "oEgRlt64XelzymWGl3-T4Zdv9xkM",
				"oEgRlt6DoJQyI4W8qXv_PCBuTlv0", "oEgRlt03vVP4znIJ17edpsy76KXo",
				"oEgRlt31Qd0Ff-Bq0ZELDWf-jTrM", "oEgRltya27xppnbEAHYDIsFyj148",
				"oEgRltwc_1UG0ZfoJlSFtfEY_els", "oEgRltwmthypvC6rRhs6yTM7D5tE",
				"oEgRltwUqE_1B1ASwQugH-sfmkA4", "oEgRlt7kqkhUFZMVztStE9oVkGco",
				"oEgRlt0HCCL8gA4SmzSeUUvcfDZU", "oEgRlt6nlR8JLCgNLrNN9xsJpO8A",
				"oEgRlt-ho7_kkI9hT4zXLnni1N6w", "oEgRlt2O6jqxE04otgx-ZScZ7Vpc",
				"oEgRltxeuPi1xASp5HaBHMzGk5Bc", "oEgRlt4jMWSgOhP8npL-Qc2vYBTA",
				"oEgRlt75GkQBiqtTd7PYHp_KvAUM", "oEgRlt0Hn7WDXyk0MwP9l28hclVg",
				"oEgRlt93E0k-KBF74P1EBu0B0_xs", "oEgRltxwnLl8nDbDP9OtMcXxEdgQ",
				"oEgRlt957insX9ZN9lqhxpc1e8Ls", "oEgRlt0QSeQTD7pH1Az5Srk5r_NU",
				"oEgRlt9-iHEXqNpBC0iUgKOvjfC0", "oEgRlt1RT6PoboPP-ZC3xk9ykLSs",
				"oEgRlt-DRwbsqsochJ6DSvoAY95U", "oEgRltwIv7HZOzq5lzJyqkmtZ1kI",
				"oEgRltwINep5gGEBXYXs-PrhfuFQ", "oEgRlt5m3dAxMa-zT3SUMVPp_kKA",
				"oEgRlt-zyATjwfWxlvtCxnUunMpE", "oEgRltyKlqkacET9FSaDfmKeT4qM",
				"oEgRlt2GsY4kMppRqSOntfeLUUNk", "oEgRlt1OZU1XbhdaHbTFb1BFRZko",
				"oEgRlt-bRK6w_-Bif7wKk2HkG0ww", "oEgRlt_fGTqikTTTZpGCB_4dCi9g",
				"oEgRlt7Vac-zH7kfm2VqfhgG6xYU", "oEgRlt5pZ8wNrpGt-473XDx5uO28",
				"oEgRlt-NeSDLbGcNYX1YzaAbUDog", "oEgRlt4iv2ZBj0Rm0zF7laDRFjz0",
				"oEgRltwhVzPixcYBUcwB_aBMdI44", "oEgRlt3N_BzI77CpdCKC3FgvDpGM",
				"oEgRltzqrHR7MdVKrt-YAE1D_yQ4", "oEgRltwBDgMw8wHaAcjTBrIGYm-s",
				"oEgRlt-d8e1XkAD-niZvslTcUQJo", "oEgRltwTRr6pAk1m6vQHpEukwfko",
				"oEgRlt6UDkgE4pYD4S-RwTTokIW8", "oEgRlt4D9r2G_xc1CqAUiyJclFDE",
				"oEgRlt03tbSnVX_8GXc_ADn5eOks", "oEgRlt20l9g_k9EHNwSCXCoqBS9k",
				"oEgRlt1tdWq6-FuRVqFvLPQwWyhg", "oEgRltx64vmV85IdeH60YBu01sX8",
				"oEgRlt3pH5eQ1neSvwcISvXWP7Iw", "oEgRlt4r012laFL8cEmHXgLCEYNo",
				"oEgRlt894-mspyhjs1kz2X6_nr9c", "oEgRlt_C4wMchrCsiBLvO7PfhyvI",
				"oEgRlt0r5k1EEXInvEkgdSdycXH0", "oEgRlt32OwMeNxHgzaS_gZW5wIy8",
				"oEgRlt2GrwAC6bNcRpYzjMk8FDmc", "oEgRlt_3q2epnr-IYEEmmT_0vsL4",
				"oEgRlt1-UzlUrQJ7ZWTXPcSnrn48", "oEgRltxT9L1l5EynbpH3L9DIqyH4",
				"oEgRlt7M6J2fbQOrUUDFc2wtTdCQ", "oEgRlt0LH940-TbP7ucjy4aiKg9I",
				"oEgRlt0xmE0nNmQokovcF0U585Kk", "oEgRltwSFK3EI3bdCj2ltGaSK0Q4",
				"oEgRltxx796p6LK9LG4XAQcOL1T4", "oEgRlt--l9C37lqdONk87rY3GAIw",
				"oEgRlt5If6vmUkjI95YUe2vjzQfk", "oEgRlty9i35HNmxgKmN35cGk42HU",
				"oEgRlt6ntHtSBmJZqoBd2wSJCkHQ", "oEgRlt3cc7PR2ZJKNueEyATRfXMc",
				"oEgRlt7fgVnGVWK1ubxcuUwS2iag", "oEgRlt8agh0Mg5X0yLIZuoEzLllw",
				"oEgRltxXNbqx4PTKFs6jzjJF-kaw", "oEgRlt70eQ2nI_cJHohhRymRiz3Y",
				"oEgRlt414b6wlY7bouUoS--tNKbU", "oEgRltzzcSJB-VN686cmE507fGjY",
				"oEgRlt52pUA1V39iW5mAOsL2Ax9k", "oEgRlt5ZMhh8x6nH9RWM7q6-VLFk",
				"oEgRlt__4vm1deiWE03VSYj8DY-g", "oEgRlt1lP_CVf_2inpZ_u5oLgz-Y",
				"oEgRltzMTNgHOZt5XZGbNDpdVCWw", "oEgRlt-zQ4vO7a9QuKki4C8Voy2E",
				"oEgRlt9rnl2mZCuSZ5UF1b0B1GDY", "oEgRlt-G52DxYFoyLw6xbqatP6M4",
				"oEgRlt-RarpYSvKg2yGlJjgtNShA", "oEgRlt1qN0rYUIeebJAiySubvXRU",
				"oEgRlt2ASr-8G8IvCjKtWCtt1Cws", "oEgRlt85DZzIdrub0S9i-6fA1ckg",
				"oEgRlt82Frrm0sZGYzvaHHyI9opY", "oEgRlt84Frul6fw9TG_ARmJ8MRDw",
				"oEgRlt2Zo64zD2JoL-2h0JaCdi6Y", "oEgRlt767hgs4FmfpP0QpFid2_ZU",
				"oEgRlt1VioljPzxD39p2Tl_vSxRY", "oEgRlt4jktlpZ8QO9qOvbeo1puuw",
				"oEgRlt0kjiblSwBDs85ImABsb_Cc", "oEgRlt162_FE5UkoE2jD1bfUiI_E",
				"oEgRlt4eopU6Hzv9QETdoitXsPqE", "oEgRlt2f3prq9UcknydlfZo3hHkk",
				"oEgRltz7KW9i1Y_xPLoLuUgGnjOI", "oEgRlt9ZkGSVW43rihI2ixcvGhME",
				"oEgRlt1_8DTGkvcXy6lhAn5yQmkM", "oEgRlt3M8uDgxupXp_V1l3k-oJh0",
				"oEgRlt2XfmJ39_zjW31u0DpZTBzs", "oEgRlt4IA33jQzxi4T9mjeUgtFdM",
				"oEgRlt72gIYcxJLVALQWLTLditrc", "oEgRlt3z9H2YaJZgrfd67SBc7k7Y",
				"oEgRlt2xBnXlP-9nw92yYvygj3b4", "oEgRlt4J49mJM_nZyt0Mqy3UXw6Y",
				"oEgRltw7rUwnBXdHUqyz3dyqaGTs", "oEgRlt4bupSGK_lg3_hK7SWk78LI",
				"oEgRlt7e2LgWRWK58yQ349CAx2TQ", "oEgRlt4v9h2nrAP1uBLKf7ktX-p4",
				"oEgRlt6-r6WOl6K08kL1RUXywyLc", "oEgRlt3RQHcELsEWhxdkd9f9NxQE",
				"oEgRltx2EDio9TZgYzf4Utasg5Lc", "oEgRltzCPXE7ini7_QFLNjV11ELA",
				"oEgRlt9mUj8pszSRietm3z3RbeX8", "oEgRlt5CaYNImxh-nAZSKGh6jBT8",
				"oEgRltzImDGoO6zEyxE_-pvu0Zts", "oEgRlt7t6Cr8i8OGbylka1Mym7rg",
				"oEgRlt9TQD9HpuNkHdXUReTZVOBU", "oEgRlt0L5FMyOSxrp8AyRErejJqE",
				"oEgRlt_ClLGZmVyQJI1T2HNXsaYE", "oEgRltyW970als7PVbZG8EKw9Myo",
				"oEgRlty_ZNQkzX_q7n_4DyOWfCLE", "oEgRltyz37-Qv71aBh82u-SRN_o0",
				"oEgRlt3B_f7siYmtEspHuxK-P-0c", "oEgRlt2inniAigVtf38jJj-_Y0os",
				"oEgRltw8fnLidcKEFZAQXI46R5cQ", "oEgRltxE_zvQab_snU5F4i_BmtEU",
				"oEgRlt-OkToNdfNVU1LFz_8jNKMo", "oEgRlt1wqhV0E5tWLSGvJ_Qc8l8Y",
				"oEgRlt8Ks8_BeRIswBTqtN7ni3Kk", "oEgRlt7ZoFIafaQ-dv04NmAHglDo",
				"oEgRlt0isyvRu_TK11c072NVklfE", "oEgRltxDkbiBYSxd-xJmZUjgPzlI",
				"oEgRlt2tZm9dmus6ZE6AS-dP4eUc", "oEgRlt_WYnekBhV9ptk8r60Ej2zY",
				"oEgRlt1fLvhh1Lx_0SSGrknWwIsg", "oEgRlt1bHnuowVdo1eIob3AQC-_U",
				"oEgRlty-sL3okaKKAiKSj52O7MGY", "oEgRlt0VB2wH4vGCDTM9nGmoM368",
				"oEgRlt2JWk11T3XBBW7HGzD3ucGs", "oEgRlt2JWk11T3XBBW7HGzD3ucGs",
				"oEgRlt8Zn4bjT5esv3GuQ0LRH-AI", "oEgRltxAy5v-6Vzjc95QBFfpX4sQ",
				"oEgRlt0CCovjf3DWE4Fw65S45ZXo", "oEgRltwTYSW5T3WZtUkGAmy5iGuI",
				"oEgRlt3lmYqM5pwa7gpNPtkLXmzA", "oEgRlt8JraPXs2Kha5kcN-AKMJx0",
				"oEgRlt34roqGVAwc7GcdORAxeitw", "oEgRlt4vd8JUarUsyOBS1dKU9N0o",
				"oEgRlt4UnjokgfB8Cor1Bxh9e_GM", "oEgRlt8KcCdO27D7vBSdKj-7V7Tc",
				"oEgRlt9ALLpcP99R_WE8OqeChT60", "oEgRlt9hrRL8ERgb_FFhFsCVqEsA",
				"oEgRltz84-2mMhjbxYnvcQ5jIfDM", "oEgRltzSTZiW9uAPpZG8qNBVe1Tg",
				"oEgRlt3o0bvntkLxdLwwlF9gjars", "oEgRltyX-0XOQ7UykgtTgklkfkX4",
				"oEgRlt4Q0xNchyW0Etgp3HrhKTGM", "oEgRltyrxV_2S2D16YaeM5p4ssNA",
				"oEgRlt1Bm-EXFDeSo4DeM-6zjfeo", "oEgRlt6AbLIjt6Uup4UhDVgw131Q",
				"oEgRlt5dy2Hy_DI8HJ667tNXo228", "oEgRlt2kVtEPLCA5YYbgNsEhmr3g",
				"oEgRltx3AfuFS48rXXGS8p7HPLP4", "oEgRlt0nqgLpvWkZqwJ2MY-IU_Y8",
				"oEgRlt-jbfuj0WCtyPf3--Yeujyc", "oEgRlt59Lb847hZJemKF1T0VUnWw",
				"oEgRlty5VWJkt5NqaUXH3CVJdLeA", "oEgRltwyXJTilIQBvYq1c9nq2Z34",
				"oEgRlt_NYzZJD19QRXn4XxyS9RMo", "oEgRlt7Y3T6tFjG8khalX9GUvQDI",
				"oEgRltz1HY9FD5eUpu3ehpjGe9gI", "oEgRlt-a6oJNg1lT-45bky7t7ylk",
				"oEgRlt85BfuzD0ZYgN502gfNTD18", "oEgRlt-Knx1PB-CvZ-vUg2C1r46E",
				"oEgRlt6tpjK9Dq8Y8fQ5nOBIE0_g", "oEgRlt56v29wPyyNtRchMFIhTEgY",
				"oEgRltzrN0bgPC2gPGMTc2bBlo50", "oEgRlt5keUHD2o_okdnBj8baoHb4",
				"oEgRlt1G7kzM-rrBB8EmT_3Lu5oM", "oEgRlt638RLe83uuK_zx76j-FVY4",
				"oEgRltyHsin94Ie0OPxLBKzGTMos", "oEgRlt2yTeqRBY_opnkC01U8Eg7U",
				"oEgRlt7648AMaYQDdbmnBXCG8_Yg", "oEgRlt50qq-RQUvOgePqjoMyboH0",
				"oEgRlt35OhPOqC-swJJTOq-fXU2s", "oEgRlt4ZSe-FwhVWV-BLnz_LBOws",
				"oEgRltzNGuTg_pWGA6ZDinoz-G2g", "oEgRlt4ZCJQRWc7gZRv3navZjciU",
				"oEgRlt5mS2ItVin-G7-0ebMYa6tk", "oEgRlt0FVdmIxJYUZAbu1xOf8WWY",
				"oEgRltz57s_G4bH6yh-rMAExSP1A", "oEgRlt3rkQzpvWLEmgBjr6XWI8TU",
				"oEgRlt5eDne4YH6lRCpDbYesczWY", "oEgRltwKus2YAwcd2th04r71jt_Q",
				"oEgRlt96K34vqEKqYUQxRCCgJdQ4", "oEgRlt00lGO9_KYEnEG5EWStDOfg",
				"oEgRlt4pXlc6F3QhEse9mRsrstjo", "oEgRlt6ZQwG4gwYPovuDu6JhPDRk",
				"oEgRlt3kcMs6N5zfJ5nbZyDIIX6c", "oEgRltxVu2lqiRce4MlcxBQGLZeE",
				"oEgRlt1lBv9OkTVJyk7cD1LvOGvk", "oEgRlt1D0k0o-bB2Fvkfl_eiPxCU",
				"oEgRlt5z2Up-ygGT2tHGYGYO0EoM", "oEgRlt9AXaqkJutGSH2Jl1FPftdM",
				"oEgRlt2xkeclywnoBKCBtX4q67-4", "oEgRlt36oIGbAmN7NmMPBDMcKqR8",
				"oEgRlt7ed7rJ25U4p9qdfJvfQ5jg", "oEgRlt3bmITqLHYQ-zpeQvxQtJRE",
				"oEgRlt2rlwQbjDMeFSE82SuXDpbc", "oEgRltyNC16JoUHKJ1Vv2ZI9_jJU",
				"oEgRltzIQOqzcPamg874GZ889gPc", "oEgRlt77AM-YT3uD3vEdbITbK_34",
				"oEgRlt2zNGLtSLdiqQfx_SUOU3sM", "oEgRlt1UcvS2-O531liWsAK5GH0Q",
				"oEgRlt3a2gNm8krLcuFGm2iIrivo", "oEgRlt57dYWuzf-5CKaPZebMWDEw",
				"oEgRlt9lO_uJ_J5NoQGMY994J7OQ", "oEgRlt2vgxF1WS2_Jy3KbtzLFQfM",
				"oEgRlt06RnJiqdzZc4nLf7Ubf494", "oEgRlt6SIXn0OL4H7Ds3-xzGXEtA",
				"oEgRlt59lZd4YW4hc3ignCNOaJW4", "oEgRlt1hreYYUCZXRGxwi6OApEs4",
				"oEgRlt3JC30KsW6GLJ09wWJddoxE", "oEgRlt6FIuAwzOMA8Ka26Ffw9HWY",
				"oEgRltwNwW5POMLFy4PC_mTzHxUw", "oEgRlt9VswsEngfd3r_BsrXnctQU",
				"oEgRlt5aFkZnE7ve4mBya5pjHX-Q", "oEgRltxxilNSJEyh0HxN-02-wbck",
				"oEgRlt_g7bQSPLHtoXeRTIl64t6E", "oEgRlt_nLbDtfHepbnqPaGQJGKNk",
				"oEgRlt_zHyquF0Ei_msnijfuitl4", "oEgRlt2pf4xL3QYASUoFSapm-UFQ",
				"oEgRlt4oCsRRRJs6JJeZz1ucoAtA", "oEgRlt0XwSNL1WOTQVB7EfQHcA0k",
				"oEgRlt_iP746gP-Fn5NM46aYQoPE", "oEgRlt_Rj0wGkNo4pn8TtIeMPzU4",
				"oEgRltz7QmvWcuru4Inny6GzpD0w", "oEgRltxJ5yMZIXGHlUoAnWM4dYEY",
				"oEgRlt7B50RPQIvTIIldXm95AdB4", "oEgRlt-23GjEU5uj8cuf8JF57VrU",
				"oEgRlt3nustqX9-leZTmuWE6WSSY", "oEgRlt_tDOtMAHub25xcqAiGbJmE",
				"oEgRlt9d4t7qJmYBHCxW8IdjZqN8", "oEgRltxXDhdFYbeHQO2oYrLCjC_E",
				"oEgRlty86jxN87V_1GXK6WD-hPZk", "oEgRlt3sukyJdzQeScHxsK3kCzQw",
				"oEgRlt9vgiZK_vhNbyjn2YZvfFIg", "oEgRltxrlJjZ0h2UGAaiCkuDVevo",
				"oEgRlt6C2_Soc5khXK7Kj9FXaVq4", "oEgRlt4-CaxwnhmEUExc25h9OoTo",
				"oEgRlt-seGFHoDBNGXhSgg7j9UiE", "oEgRlt4yXRmr-MaggtPwb9zEkSkk",
				"oEgRlt2FEaDydBIu_B9YQ3ksNrKw", "oEgRlt4JaSEP0S-suGtUdZtVH7FU",
				"oEgRlt5I4Lxr1nwTRHetP69WirvY", "oEgRlt4na5qfwbZ7AgRbejjE_sMU",
				"oEgRlt8GS17cznpKpWvvmtuc0-Jo", "oEgRlt37xlMgDwlOjCZTARrGHAVc",
				"oEgRlt7NktyblSeE_zYojG9XHmPI", "oEgRlt4EM3RAXboMScvXdjOPS1hE",
				"oEgRltw7P6GgrYRqAIaq-lXrxqTY", "oEgRltxjuFBObUaLhLvuKFa38HZk",
				"oEgRlt8RzqMOzxwSq7oNyyOu_7Ug", "oEgRltwP_sk_FE6hE2724FoxkkFM",
				"oEgRlt0bupy4FRX5lCh-zgkX5t2Y", "oEgRlt7BNiPTij4mQU85CGQHL6pE",
				"oEgRlt9Oa1XTHlwIKaFGmGCXXbHs", "oEgRlt-Y4ABinV3HRMZVxcZfyspc",
				"oEgRltyueULTAeRZVLCoy8Wkgsl0", "oEgRlt99B_K-AqMo5wI1bUSKFshQ",
				"oEgRlt2F6IOK5S-q16ZZ5Dm6eFMI", "oEgRlt6_1UtwBHhgPPpIGFWSLhSM",
				"oEgRlt-tVjQ6VMUyGRXzTsx5AH7c", "oEgRlt4c4_8qMJK4OnCuWK2ChhCY",
				"oEgRltxcbN9XPUqy93xj4pxWy6EU", "oEgRlt6vSMdPLd_cxp6rmbgesu8E",
				"oEgRltyOkeImWXEF52Ue3rowHboE", "oEgRltxSSVJzPMnCXBMBi25S_qf0",
				"oEgRlt0A9ff0H0CAs1gbwsoADM88", "oEgRlt_PFR1wXSP5keWJ_Uc4AQzU",
				"oEgRltwLmJEG3SzKHQBoJNPg-Otg", "oEgRlt6jBvNLWcsQ8uj-wpIVusuc",
				"oEgRlt8SdhCrC5CvWrZGTTmcvwsQ", "oEgRlt-zs4__Km_RbiUZoYALgUkY",
				"oEgRlt67F1MbdJozzmv8jIeJXb-Q", "oEgRlt7IIbyPiw_E7fqxBIlMQw0M",
				"oEgRltzkT0wBDwMSC9r5C-p_RD7g", "oEgRlt8ofP0GOOlYY9DC2MJ8DD68",
				"oEgRlt5FvpCJBlxVra8KTvMFqJ-A", "oEgRlt5NN65xyzaCYO8fyGin4zVA",
				"oEgRlt8k7gZEgXv1RVJKz6aFynHw", "oEgRlt0O-TdARLqZB1lIouUep1bo",
				"oEgRlt-fxnFWuuH7BS3X7YZ549YA", "oEgRlt6wa_SjrlrNAhuNT3L51GBk",
				"oEgRlt3rgwXjKBO3_Sp77giVO7Ps", "oEgRlt1ClIh05QiS5Agsifk4aSGA",
				"oEgRltwboMeI_mCpCeDOGejgag1U", "oEgRlty-nWN5W43pG15dQ9N16AU0",
				"oEgRltz_o2x8KqfMB-ZGESYo_qqo", "oEgRlt3SHx9ELpGhB1Qtp6DxpBtg",
				"oEgRlt_7fW5DhYylhE8i3WyCTlns", "oEgRlt0Ox5ZrfHPLXp933_7PHpzU",
				"oEgRlt4W3MfiH7jb0Sx5uooq2VRc", "oEgRlt94rSLlINy7NZvarIZNewMw",
				"oEgRlt_r1kyZhFBcMpUs1bqKzdAc", "oEgRlt9DyUUYdEwO3WvMzk8zHJTM",
				"oEgRlt-ddZTljvCB_XW3oWYin1Lk", "oEgRlt5QCtedWTEF8dZRtko9LSfU",
				"oEgRltwF28lfCkspHoZJL4D_Qbf8", "oEgRltx8178ScK2gJNh0ieIJhFI4",
				"oEgRlt6LlhtQyZy2kgf210MotjVM", "oEgRlt33ABL1oITYGzLn7oVkeWps",
				"oEgRltyr9TytWwjzgVCIqqnaj8_s", "oEgRlty5pGlIL3LqcSsniBFEqfIw",
				"oEgRltyrqFz1Qw9sVTvps_nN1bLg", "oEgRlt7RZRa7YN3rrz90LvtA5WUQ",
				"oEgRlt0xTqHjlTy8Xkaz-lsKcsdA", "oEgRlt4jEI8R-6NhnEhmbeptrTAI",
				"oEgRltzesJugxwKgPL_47mM0XQ-I", "oEgRlt_6osFver7c9x26bcCpgJmo",
				"oEgRlt3yytzZCFTCNgXN0D2a-HQE", "oEgRlt0hQTdmDoUIEN4hVQpPKF-c",
				"oEgRlt_Iu8WgqzzyvCboyVb-cHK4", "oEgRlt0GSAWrfY9e4nr63gJ1xY4g",
				"oEgRlt2Zc0LF4EILt3WTxhux6cdg", "oEgRlt1QJzIhsWqpZfmwf6MVyIlU",
				"oEgRlt7kCVwTOBaF452IbdZT1CP8", "oEgRlt0_gDqbVZNKzoC4_cG7QFsw",
				"oEgRltxWqhvoIDqWGkEb9ENHd5pg", "oEgRltz1ZzVPW1TYQ4FoIRbuhwgI",
				"oEgRlt3y3gKsB21YE4HFHKY52KUc",
				"oEgRlt-AR3Fe12tUhbsBHM-ly_S4od2SBjtLN8wUKH6QNB2e0MBtJJng",
				"oEgRlt1OP9U1lUmcMYP4uLLh-3Tc", "oEgRltz8ME2liqSeYpGc9KP5XXxc",
				"oEgRlt4Nad752s8qR1zwQd25T9u4", "oEgRlt-XRCtmIWErQqBjbd-byapI",
				"oEgRlt7yJ5kRnja2bJ1OXz3LOwTw", "oEgRltyIIkegzrwby_kZDhywNuAM",
				"oEgRlt7Wf5OOFnUbERaGqTjsBxlM", "oEgRlt3sFpVd1dw7GDaXdzLcd_Z8",
				"oEgRlt5UQ9mVKK8lAUIw0g1_SzYQ", "oEgRlt_GZZ1auYmyOhxWASCNRiaU",
				"oEgRlt1w-P3yI4dAk7UOh09JbLRU", "oEgRlt-LmWeyCPLAX7wsF1oeVbW0",
				"oEgRlt05FncoJ7iI5eJUvruuVBY0", "oEgRlt8MG0MDXQ7ubWP8yqjnCHcc",
				"oEgRltzglt8mwbGG3Lcs5cDbp6fQ", "oEgRltxEEWCMwtOvqnoA5HnuUcDk",
				"oEgRlt4uS_md5AXb_wkcFGMLBiLY", "oEgRlt-DwURhFmQrQpaQzp-J3nl8",
				"oEgRlt6r6boKPGKUv0iVA6IjRKE4", "oEgRlt3kiG6tT_4ARXA3reif-2_o",
				"oEgRlt9vFvVbd4bGcR_IR50X3NKY", "oEgRlt9YS1OqN-mIw5bKgA1YO8zY",
				"oEgRlt8QukbGC-3qJKLQgXn5DXy8", "oEgRltyWoB9w5I-oFIPO2R4ukRLQ",
				"oEgRlt5Vlex4_1vWVtS8VTCKSy_k", "oEgRlt-3h48gqr-ltr3JwYI9K0jM",
				"oEgRlt4zt9fYu2yiHS3wkhhTougo", "oEgRlt6oJBl0oSA-ytZAGcn9XETI",
				"oEgRltyWaw5eUZ4kv9LRGPfj68Ns", "oEgRltx12OOt1jhcSe7YqSfh5AkM",
				"oEgRltwFh44U8a2OtVSvSFXcx8-s", "oEgRlt6cKo1cnVWuCmNHe8Sb2e4Q",
				"oEgRlt3IUIpd6EwNtF6ahp3C9i4c", "oEgRlt6nzQG0p9Gcmt3BDAQWA-bE",
				"oEgRlt9zfn1_7LDMMsdkHtguyodg", "oEgRlt-TDj_oHyZJl_p9qCLg66Ko",
				"oEgRltzxLJDrKYaoPnssXeigggUw", "oEgRlt0vrOmW6TrwpQ2zS_3WPZCU",
				"oEgRltx7EDHiT93JfrBe5NOt5J9s", "oEgRltzzAJhhjRiJCGsc3RqHwvq0",
				"oEgRlt3wSDThZVuKayuzJSw74nEk", "oEgRltxYfkYp2uh13UVymzJofI8A",
				"oEgRlt6ni5Me090WfhbIL9QwuCH4", "oEgRlt1J1v7bSTXMjncFBUP-aq5A",
				"oEgRlt-7QH1ILEDlv4IO8QUm8gAc", "oEgRlt3jLPjWHvNUc0_lqtzM4Kco",
				"oEgRlt-V54_reKQkWy6vCmSyqhnk", "oEgRlt_pNAQdXNX0yTfjeXnC1kpk",
				"oEgRlt3JpeNGVtgJ67YqkNMf5aD0", "oEgRlt0FvC-c-XquZHgUGZUhhoys",
				"oEgRlt9q21YwkUZ_kSR0ASzkjuyI", "oEgRlt9lb6cks8FV6M0ch0-sm-Ts",
				"oEgRlt8WadD9Q49zlXlukguqzATU", "oEgRlt6ospEwixlQFDotlC7z05rA",
				"oEgRlt3JL40Zl9v3co6abkqyghqE", "oEgRlt-n62f9tbIXbA0km4EvoFAk",
				"oEgRlt7JBDZAHlz4B7ZdnkS1b4rg", "oEgRlt96AzUX5JoWvjhYbvk1G0qo",
				"oEgRltx8LG8pgIZXlmgAX9QserEs", "oEgRlt1CQHcRNL611HhG1ENUurCw",
				"oEgRlt9oNe9yzOX8oS1wKWlCABk8", "oEgRlty2navtqahxzqNCk6oRLzgo",
				"oEgRlt1ee0GgFIcNDFgE7GxhnamY", "oEgRltzReSDdkQFakxznLj7b2dfk",
				"oEgRltx7sLkp9Rhb025beUwaHImA", "oEgRlt4M3W6PL2ZCYlf79IA8el38",
				"oEgRlt7R8lfoZP54FqftiP2M1XPE", "oEgRlt3n5hbj-3QVZkJd-Yv5TKTI",
				"oEgRlt4WESAVlffPwPGoI8p0S-b8", "oEgRlt7MNE9jRwaCn1hV2x67fjzU",
				"oEgRltySCN6qSg0ToyKSA3g9mSFk", "oEgRlty3Dw1-x8gmX9NqU9uuYsAg",
				"oEgRlt_b6Ovnv_36D0zTR8isJma8", "oEgRltzXht_XM14um4k-I7e24hFs",
				"oEgRlt0ky9rFYrt2Zea9htp65eZs", "oEgRlt6uIJUvl7F37S8CiA1zeN4I",
				"oEgRlt4PGdFHq245e-JTBl_u5ufg", "oEgRltzrMv5JVkxFSNY5qc8ysS_o",
				"oEgRlt9f3-_hyW060EmbOamgxLs8", "oEgRlt-CG4wMUaGPQcyhMz9xB7qo",
				"oEgRltxVjffYSmuXMT0VH8qB1YeA", "oEgRlt_OcRP-l-1UcgOmp6cClLSM",
				"oEgRltwz16PycnRZTDHW9yKmE1yA", "oEgRlt0s5oaUuv7_iub6-XuQbBtc",
				"oEgRlt2Ckq8uXEtUpH2sey1h9j7c", "oEgRlt8UtSe9Soo4gacCIdGcuMKM",
				"oEgRlt2FeuQX3n-j-ZrOYlXxW8e4", "oEgRltyfjvkWQGw5KO3jtxmXDRaU",
				"oEgRlt6o2PqS4F5BgXjlsa0D0iRg", "oEgRlt-Ocbb30kMSnsnKNBVFfXF4",
				"oEgRlt9jIBuoHl5fUACU9j1es4rM", "oEgRlt1Uo0QG2gK2y8zJGH9ix3cw",
				"oEgRlt4LjhfUuoSToSujTrQDKpBk", "oEgRlt7ZjCy0OBJhpPkh3u3g2UVU",
				"oEgRlt7ie_EX_6gzhGPsPtpS6Dyg", "oEgRltwlub6zYdCgewf-krGrD8DU",
				"oEgRlt7vdctWsvMJZOsH22UbO4A0", "oEgRlt8AGCBzlIHGzFR9MnBjy2jg",
				"oEgRlt_0wqmPuwXIaPVBDUwbek90", "oEgRlt4hWgJOg_MBhCHqhvarBO4Y",
				"oEgRlt_S6OgaHq-fnyLLH4DBSl3A", "oEgRlt25whkugDImrA3EpVjxU6fA",
				"oEgRlt3p7-Y5HbMzxCh6COwuoCtI", "oEgRlt2R4ikzJz9EK58FhCa9nsgk",
				"oEgRlt7Z4xTk10GDAbu5VpMFHhjE", "oEgRlt8Z5Nu-9hVXeAGsc2MsLqjs",
				"oEgRlt0LlugPvdtSbqOoceBGLPP0", "oEgRlt8ZQIoRRp9O6xAsM9_LeqA4",
				"oEgRlt-_ALaS1Pz_8V3BRA6Ub0Bo", "oEgRltzBODPXvWQ9AjBARUsJSwQc",
				"oEgRlt2VD_2_itcG7y-Jak29iCpM", "oEgRlt3pixVITBC7U3lYfR4wxoI4",
				"oEgRlt5Qrja6H-9Nv4qeZmgynUb4", "oEgRltwqXhk6C_fkA-PULyWlyEOI",
				"oEgRlt3A7rOHIl9o4VoRLPLP0j2E", "oEgRlt3U88eroxy_-f7-GlKlxkMM",
				"oEgRltz6Mkh3r6uanaABkjXer38g", "oEgRlt_Ji-yWAKd8AZfH1e_sPi8Y",
				"oEgRlt6HxbPwbp28R6zr1ugvKdc4", "oEgRlt2S5HlGDJySSx4rf42tRW20",
				"oEgRlt4fcxGMZsdQS6g8tVgScDkY", "oEgRlt_xjRVUEnIiIBFh8racfUIY",
				"oEgRlt9eg7iH-UaDRHFytj7ijYGA", "oEgRlt88CsSaZX9bqBnjBiLoo8A0",
				"oEgRlt_p5Pki5SoeOMmDW2H9E6xY", "oEgRltyIpj4BPn1dCGhbhtC6VSQc",
				"oEgRlt9A_rhgyOrZAXM2NGcfmcjs", "oEgRlt83DEac8AGTtFbRSu3dGsSU",
				"oEgRlt-1Qwa-Acx2ZRPkTRgOegME", "oEgRlt8hX2IRcXBvQqDHkNBt0_lk",
				"oEgRltw-BE8wKzDVVXzZdNsPLiS8", "oEgRlt0WQSNaNhbuQSc4ca2PpoZM",
				"oEgRlt4NC54rrEr-nm87Ka8gnCYQ", "oEgRlt-uFE04OcJsrCg5Lp4Fvw1Y",
				"oEgRlt6MOK0y335tY8gmDy658_AM", "oEgRltwN5m0IcKjrZNvqXvqJ7g_A",
				"oEgRlt5m0aVMyLxi__6pBnz4H5EY", "oEgRlt2jYdOVXWYLJxEaYEETx-G8",
				"oEgRlt5-6UhhRPv33kJSiZGOs4gc", "oEgRltzlAUPsMo2cOoABGF360ZT4",
				"oEgRlt6vF05gV9zFFa5jr4OkFuJo", "oEgRlt78631xr8Ncb5_0ISYLCqP8",
				"oEgRlt1dXipzgSXtmSE6Y_Chx4VM", "oEgRlt-9wIjIJh9eQ97IPj1H_5yQ",
				"oEgRlt3Yt5JLff47KTcqoge6oYNw", "oEgRlt2LY_WCDpGJz7MHCw1Oz3-4",
				"oEgRlt2vUK3C73vdRsTUuB8VP91E", "oEgRlt9ycz_cph94XBVNOXf-EiBE",
				"oEgRlt2e_25iiPsMRfjdbgJQ4CZI", "oEgRlt_H3Sv26-y0YO8PY9_1JbZc",
				"oEgRltw5uL9-gg_ykp7Hg3CprOkw", "oEgRlt1np5iwZlGk149IOHRgluJU",
				"oEgRlt7xcROv9EeLcsyjpIF2cNTU", "oEgRltwMlTPZQ4pkmWweDmmaj7iw",
				"oEgRlt6S47--b1uCjygcZV6J8gNk", "oEgRlt1KPHnrzGUiBlSu7Uv2yW1E",
				"oEgRltzzTG8bFBP9ohiHld5NLIOU", "oEgRltzLK7X_ldAah5uaDOnDRBzM",
				"oEgRlt1x8eDr7S9K2g8tXybtWAIs", "oEgRlt_oUCqcAuCJSrcR7pSWRq-Q",
				"oEgRltyVQteQR4EVtrFyKA7cf7Sk", "oEgRltzssDqGeBGi4iShTy9djwWA",
				"oEgRlt7ScfikS93_A0UZAtzVsd70", "oEgRltw8PFr5yq7-VWPt1B6ZlXgM",
				"oEgRlt_RNVM5GTpmqQWxNZXalYCE", "oEgRltzyaTX-nBtdEaYbpzgU7PhY",
				"oEgRlt5E-sQmNi6qEjy2kfVt93mc", "oEgRltx35I4CODs_uWchHgPUY5-0",
				"oEgRltxmCM6oWf40wklHl9yLtaZA", "oEgRlt_iV8TTkjkMWT384OIMJWTo",
				"oEgRltwfpSGlSUVrKW-kPuLLPZek", "oEgRlt3DXY5B_nT_jlyJE5dtZ3q0",
				"oEgRltwihq8SWOdBbXNUXULaSCWs", "oEgRlt3ybVpNUoTDAEW39FKoGKME",
				"oEgRlt1wlyrULB0Rt0CsHUrMGf8Y", "oEgRlt5X9SwpjSeaTUjAAOB3lYZ4",
				"oEgRlt2X-BNZwfCgYOO21IKitC5k", "oEgRltzBk4xGl1A_PymY2-fz3K7k",
				"oEgRltxcoRs6qJpK26fde8nDE5BM", "oEgRltyZ8dKgjlc3AepsY3y-dsKc",
				"oEgRlt8-lSFCYAB03E3pV3A947ss", "oEgRlt23efrxO8SGafM_r6PDErOQ",
				"oEgRlt2ed9vnpduiX_gb6z1-LDrU", "oEgRltzrP1U1re7KCVbaag9pqPgo",
				"oEgRltxF6gahJYk9Ag-r9XcjE2zs", "oEgRlt0OucJcTgChgAaJPhx8ipaw",
				"oEgRlt1FD-k1Oz2cbMZRHeZ-qN7M", "oEgRlt5FyrHL6sqDInwO2ixGCVu4",
				"oEgRlt7wv_MFSbrxE-zw5HDu5wWc", "oEgRltyGBuRZCA1z04sELuSrkahc",
				"oEgRltwINUR32VcWjv09jyvra0JA", "oEgRlt8lv42buarU88mxv4q6dbGY",
				"oEgRlt0sK4KoE8nUSPAo-YgEaf_c", "oEgRlt0num-tzb28wFHF4feizBMM",
				"oEgRltwBSWKT_N-rzCUwDGltu5N0", "oEgRlt1wuwZJmJgfmeqLpjuQQSkQ",
				"oEgRlt9s_OiDOBpXQT3qldsepFSw", "oEgRlt8Sc5CBaIlOL6_DEvsNr3LA",
				"oEgRlt_tacJsvpi90l34oQIbsZ_g", "oEgRlt58hPQtXMDxYCTMNg5Mj2GI",
				"oEgRlt2Dc3RZJ-UaXFQoJ5AsOTlY", "oEgRlt7RoyOWDG3NOO9wheTFP-Yw",
				"oEgRlty5BGj6KB-2Xb9goXm-GTx8", "oEgRlt4-OPAleoPJeGALK8rJlStE",
				"oEgRlt4SQxNOyYXhCFSJSRsje9rA", "oEgRlt82ShPNYKDzkRBhaPumCyNA",
				"oEgRlt20wCUM785JrpCTfYfC13XI", "oEgRlt29rMoq0c3P0_O0otPfH4bc",
				"oEgRlt9UO_2uGTjJLcaf4h3quBFo", "oEgRlt5AeV3k5Fl6DirX1tCLCDPU",
				"oEgRlt4RvV6aw3Ut8C879RvROfwo", "oEgRlt-MR7V9a9QAK1SZ06iDa9_c",
				"oEgRlt1SKIQtQNOwafgfmGMmxdu0", "oEgRlt04H0Pm82RApyXiXOuOY70s",
				"oEgRlt4h87ihqJsYfENCvQWU1L48", "oEgRlt8BUm9vQiMqCgi_tBLxRbcQ",
				"oEgRlt7Cq_iHr9fV8a8l5EWVuAdI", "oEgRlt6UAYwOOxwJejHvC7aspvAo",
				"oEgRlt9HUWQAP8RJXSb6Y6B6JqcQ", "oEgRltzJAZiIyRTz3RugDi6LS5gk",
				"oEgRlt71iUD-DRhdlEWPDk7f5Bws", "oEgRlt92GArtGcFq3OB3xRPpGlvM",
				"oEgRlt1f-VH9K1Qpr6ANaVnPVCtQ", "oEgRltwN6YSx4Qfh3QbfCUWCDWsE",
				"oEgRlt_ExYDKGyFFrQVWzujk1g9Y", "oEgRlt75S2g9VmqZgcnz-xyz4pe4",
				"oEgRlt3X9KWQGqmeADBRtDB3VEbA", "oEgRltwYGRPAl8ZqQf8JUYDViGyM",
				"oEgRlt0c6MnNT9zKHVJyPh4u0ALo", "oEgRltxwBHkESFTTHq_4JQLEQABM",
				"oEgRlt7e_qZrn3HSuqfaCOI8z8fs", "oEgRlt97Z1BQ4sj7wYYGCnub_Ndw",
				"oEgRlt1rcWCnIr-GpVSqO9J8Rnc8", "oEgRlt_fTPr5oTQBdgwFI5kTUDj0",
				"oEgRlt6L9ggpoOSB0pjCJqvtj4IE", "oEgRlt3-rOrUg_GyCUhbU_ZKKpVs",
				"oEgRlt4RE5HQFFlAWG9_0HWYOjHQ", "oEgRlt4JJm_LpesJpArVNl2guNn8",
				"oEgRlt2XP4okE4kLIOFv2xg1INAo", "oEgRlt-I_rIgYLrZ6CTXtp7jxAdg",
				"oEgRlt5n-sAxg964m90XBeVy_2Yg", "oEgRlt_M9YXkxh8kDW0cLDsThA0Q",
				"oEgRlt4oQAeaWnnd2FXgjr1Hngyk", "oEgRltwmn0-2eebzJZ2FeJqaM3O4",
				"oEgRlt4MhJZcEOelqbmuBVCZIdMQ", "oEgRlt1STUjMPStBr6cm33i2MD1U",
				"oEgRlt7pTJTWCQ9qgo6uSs-BBwi0", "oEgRlt07ZGAUu3KDRx9IONZsRaI8",
				"oEgRlt7XjgscuEcXzKyHhdpvLVTY", "oEgRlt6I51mWft2LkSJttzeYH3rI",
				"oEgRlt0a9Um-exG2AiYiC06rnwx8", "oEgRlt7FZ1XlAG3flcNXalKtMh1E",
				"oEgRlt4bD3xR_RW3slhWO54m07uA", "oEgRlt7g3F-sx30wb-9RGkKD52c0",
				"oEgRlt4kLxtV2ekEkcz25BjXbCeo", "oEgRlt0xBKeh9X6Mala9q3s3Ywp8",
				"oEgRlt0kpeeZTML5E9C-krFMdO8s", "oEgRlt1oa2V4zys9GZZjoe9W7kRQ",
				"oEgRlt_eC8U-cXRRrZkCP7A7kT0wod2SBjm9WoA7lJF55LzKV9gcDAlw",
				"oEgRlt2tvEtiASefT1q0EGjr7L1Y", "oEgRlt8V5lhogE5KBVsrwzzBMZEY",
				"oEgRlt7ZY1KhCX59U96rI5zcQciQ", "oEgRlt0zJpOqaf80USi_vNOliLM0",
				"oEgRlt0GfBohsFssYtXv5f8bPnOE", "oEgRlt6SKjUZ4PmmLLixLhAgcyeA",
				"oEgRlt5bTM5UTo2MXGWQxpEYaukM", "oEgRlt4mODe3WmwP-dOdU24JFFgM",
				"oEgRlt73Sg2XR_rliJcVaQ3XI9oU", "oEgRlt2djS8mBTp8slqSGXcLQjN8",
				"oEgRlt-xdo-oG3y5jNh4t00bpPuI", "oEgRlt2UUPHN8Wi7JLt4VJf2XFxI",
				"oEgRlt6KVV_lERrR1D2GZV40Dts0", "oEgRlt2jziVhtlCBEZm6Et_2T_eE",
				"oEgRlt-EGRYEM4OzRT-47mqxffCA", "oEgRlt1arYTkh-OrlWp64JOWu3Y8",
				"oEgRlt11PvXq7nhzvh8tqzlYhULQ", "oEgRlt9zhj18lP2wOb4xdveWb4Jw",
				"oEgRltw04_Y8UMUDKg9u007A1BhI", "oEgRlt7EDn-qGDj4hR_2pOGuS_As",
				"oEgRlt0Y9d0c6baWbgtuK1d1iYgw", "oEgRlt3SiGTv0G9cZ6kHZbhSXRdw",
				"oEgRlt8hPoI7ex3jEnWu0PXlaZPw", "oEgRltw9wTU5KrbGXo_ysqGuPyxk",
				"oEgRltyDXgKwcaZliEGJmJYVQvcs", "oEgRlt_-g56peB5P8MwxOb7vmtHQ",
				"oEgRltwQI5-rTTzzcI-tnF_PkQAg", "oEgRltxOWDgeYypNcdlQgyOd0ICI",
				"oEgRltxAO4mcw9X00VDTHnDooSeQ", "oEgRlt1_eSeIAOqIr2QPE4dzGapI",
				"oEgRltxlg9zAHVxYgoEL78U49fLk", "oEgRlt20wZrjwWKQUPdo3CbqDuOA",
				"oEgRlt0USwGAoJlXeng9zM4WTAt0", "oEgRlt2mQBeKsMogIiZQE7jbJMbc",
				"oEgRltx8aBt9nq8eG7m11o0iQe5g", "oEgRlt9ioYwCnrIZGO53_lsj8S-c",
				"oEgRlt3F7GZZbdwETn261-TDJidg", "oEgRlty2M0Wzuio-MK7vLm-17TW4",
				"oEgRlt3hvh1CspEnGN96SNE-_bd4", "oEgRlt9RYYZ7hTEGi7WKM2g4jquU",
				"oEgRlt6foxX85gP3lMpNxCd_CunA", "oEgRlt4wZ8JJ8DFA27EXpTsoqN9Y",
				"oEgRlt1Hw7D9mdg22Dxgf66VaAw0", "oEgRlt9thbMFrkDpGP0hOL2iC7Nw",
				"oEgRlt4cIGkc8zBDyY-05Y-o_iVc", "oEgRlt0RlJUr_Ff5wLAmmNujqGDY",
				"oEgRlt_7kz-6SPPGNaHvbs1D4tdA", "oEgRltwaRu1F1eboZU3xjc03oH8g",
				"oEgRlt9mdNGYRsghLPjDhovBXHiU", "oEgRlt9jB_XoX_yEjYR1iQ50OZYQ",
				"oEgRltzpo1OREUGo0AAmuX-eAKlA", "oEgRlt4OxEj5_98nYTxs8mzL-h8A",
				"oEgRlt1IWNRYf4Qx4mnJkHZq-D9E", "oEgRltww7mF_4tsMDtGZAn1t0dLU",
				"oEgRlt8bbqwUENJFy8MmmC9cK6To", "oEgRlt3NC-V1OpMNksqWM26irpys",
				"oEgRlt8ywMVjt49RBxx5Y2zC5Uh8", "oEgRltyg_TAn-HmFnzRXem4fefgA",
				"oEgRlt-NUdLEl3HzD6-MRKLmJ0Rk", "oEgRlt1elEcSxEdkbMJV0_588aGU",
				"oEgRlt0TFCFFYWK2qcZUWiWFPn0g", "oEgRlt9LZmmC8mtuEVUoZF9WsXEM",
				"oEgRlt-Ug14ep0Kd7NEecT_ivUAI", "oEgRlt8GyLBtd6shLVLN48KqFRys",
				"oEgRlt34ud5bDVudCgnnPJ5Bi4kc", "oEgRltxUtGav-EWHF0pa1Wt_mC6s",
				"oEgRlt38PveT7H8r8xmbTT7muSVQ", "oEgRlt_xbzEsFCPMfQgLh_m3j4qg",
				"oEgRlt2xAW6hu0CU3-vBtU7yHCEg", "oEgRlt2wzhT_XnIYLyGFGC1An11k",
				"oEgRlt8pkj9Dbxl9cGw4jzdeQ6jo", "oEgRlt8tS1W2newlJt5MxkqYkDnc",
				"oEgRlt3VpKraNp_zIoe9UIBS1_H0", "oEgRlt4g9qe_kj3G2TX7XRZrik94",
				"oEgRlt97d64Wkwjao9awfc0lW2rI", "oEgRlt6VxOSSF-DqCJr7wSBrYjkA",
				"oEgRlt-Jpjrfja2_oJVK3rTW8iDM", "oEgRltxP8366QD7y_Qp86GNEaldU",
				"oEgRlt_Rak5eKz29hmqdt0r8kFH0", "oEgRltyg_7dA3u__Fu7NRyjnsgYs",
				"oEgRltwRBAHiOiOD4M9P0xoPP91w", "oEgRlt-h_F9zdGdYU41a5Ica9fIQ",
				"oEgRltxkFw4_aQXcHQtnYDckslFc", "oEgRltw43yBnN9xtJRUEcZ1URYMs",
				"oEgRlt2EZaMHvVkN4TWMRVhndous", "oEgRltz517VxFd95EtS0sH9hMA-o",
				"oEgRltzvrJlBORL9uPPQYLcdnO7Y", "oEgRlt6PdOxF72mY3Kps0O0ARaJU",
				"oEgRlt1KrvVKbGMQ_HIEntqPgdN4", "oEgRlt-H9dJh1p07CvDejr2ZW2h4",
				"oEgRlt2N2cnP4KyDXJUPa37gRhpA", "oEgRlt6UGiYHgD4BO_XkpYIj9YI0",
				"oEgRlt1rN9_6mBAHn6IiMCkhfalE", "oEgRltwcvSMS-CBZq8vIggEvbXTg",
				"oEgRltyYO4E_EEVTJbeQTpABOF48", "oEgRlt6vVEm3yUbFppMibnO6vhYw",
				"oEgRlt3igq7YvHTKM4iWIbkx5KrA", "oEgRltw-OyTgpYHPSp_4EAMijPP0",
				"oEgRlt7CwnJy74HPAjTsyLFx_r9g", "oEgRlt1i7npUKDkAQgj4tO36PMwQ",
				"oEgRlt5FInwMMEyOrCx34bWOif0I", "oEgRlt37kM4bthOfbO2TMjIvGqH4",
				"oEgRltyNaCvdHqIeSlT6fzuTus-I", "oEgRlt2Vy5hTRQAQRR4z7UvrYbuY",
				"oEgRlt5KN6qosZ40bj5ANz_rxMBY", "oEgRltwCPDFwneLqwILtxC8PYbBQ",
				"oEgRltzbm_qlXEv-l7V-6163IUQM", "oEgRltxzIkQZmUcIMjkmVKvHmNFU",
				"oEgRltzlpvTTqBzTF-xlUQHIzL7E", "oEgRlt1wO5h7cCaa_VEXure0neZ4",
				"oEgRlt8nWL0rS-hPKmewrjrXxnwY", "oEgRlt5gJu7ayXneUDbdtVmp7bt8",
				"oEgRlt5PkCs43OgTf4SwdSKnI_MY", "oEgRltx8qAFKMQz5HC1Eugpmy5Pc",
				"oEgRlt6bsBEOSb3dm99tN9NQqBNE", "oEgRlt8ZT_RvyyQ_uJeESHMd9d3Y",
				"oEgRlt7Yfv5ibAzJLGWzWKOANJzg", "oEgRlt1MEJ4DeLCWMIRlsz31J1Ok",
				"oEgRltypLf201Tviz0o_J3tgXJDo", "oEgRltzCTCp4y8zHIgelClJyV1OU",
				"oEgRlt9Q7qFY3Zph5AtyUe3XNzi0", "oEgRlt3cqmgwFSvN0khw4WKhrGas",
				"oEgRltw2dG2UTElm2MTNRGbwf4hs", "oEgRlt3LIS55SVLatThWdMW9PAqE",
				"oEgRlt9KnAXOmu5AcoVGlTq9fbUQ", "oEgRlt2F8i845hrQjVLyS7vBZmrg",
				"oEgRlt-SoluO2HN_wR_SLxknMySg", "oEgRlt2pg9ncGaNZQr6RC5CpzV-g",
				"oEgRlt8DvBLZ2QoXscfE_5A-GqQw", "oEgRlt7vIhpNh7QDVtAabORit1HM",
				"oEgRlt00gkBp2Ge18up0YJzPHMw8", "oEgRlt3vDjSMF9engisv2Bc59hXs",
				"oEgRlt_gPKQ0viDTYImp0PchFqus", "oEgRlt4bAzTZKIooQKcSM6l0lNU4",
				"oEgRltxNCFsMEZ7Sxgs4VAjlLSZk", "oEgRlt9P2M5VQRp0sfti-aMntFfQ",
				"oEgRlt-dxS7S8kL7ARMRKCZh6VXc", "oEgRlt03nNSqiHumSqViCMhh2_Zs",
				"oEgRlt9H3-VVgcAd_fEPb6nTpcdg", "oEgRlt08q70oXdVrr_26s30hbiFE",
				"oEgRlt9KY_so3B085nFoGloP3aVU", "oEgRltzxX1LBPb58ccigjHUP7sYc",
				"oEgRlt_AfUfnVmb0rlmoVDGK6jRA", "oEgRlt2dBBsWpkUpMGZtCJNhC4EI",
				"oEgRlt5XZ_52iP_wsSs_lOh08X2k", "oEgRltyo1Shid_9nepK1qIBMridA",
				"oEgRlt_olWs1fwwwLhJefkuDbhQY", "oEgRlt2k-bUuS0tC7s-0F5NKyzZk",
				"oEgRlt_vugc_q95GGACVBXOcVw8U", "oEgRlt6JIt_S6nsDYmflmSs2EAsc",
				"oEgRlt2Tr3HucEZBSTD6-kWzX6lc", "oEgRltxS6irB4MdbS7n3c5DVQ8GI",
				"oEgRlt1WD1zM2zpij1I9LsMgwNos", "oEgRltxV2Wj-Q6knVO6mtyptEHHI",
				"oEgRlt-QzD0YLZ8sHEBq6zmVLmyA", "oEgRltwjBZVr4Ghx32o-aD6LcfbI",
				"oEgRltxFmYdVQrloxos8grYcukdg", "oEgRlt03FpOCCbrldFYXXfrM5BQE",
				"oEgRlt2NdRVDO7hdeU5ykf3Rxz28", "oEgRltx068QaOlvTmvWc7eUMIb4E",
				"oEgRlt2VN8TF9BcwjGlUD0EBWvWk", "oEgRltwYiZbZPAQaNURgSjpd3I6c",
				"oEgRltx-VrA3AWqxMW6m8Mc2LyRc", "oEgRlt3cPh6FmlIAoMbc1xzpqxuo",
				"oEgRlt5rUNoTxlJUwH_259I3qU24", "oEgRltwyO00INYudnjIsFsmIRbjM",
				"oEgRlt65VPLpRqGZgdSr9Mnj_63Y", "oEgRlt7kA5Xracb8oxw4Q7zltUu4",
				"oEgRlt_rjYGmlI9UOqaOHYFC71Fs", "oEgRltyC7aEt0N59Ft9SaRIkv6LA",
				"oEgRlt1HAhJcAK6ff5XvdAvkTlTA", "oEgRltxTARG9LYB0H3F3BpvNyLoI",
				"oEgRlt24-GQIpC6YK5osogZAMsvo", "oEgRlt9Ywb2KP6Q48XcAYvDCnrEk",
				"oEgRltywHoPKQBsTbTrQ8AZ3cO7I", "oEgRlt4mCdRkjlIm5QWkTH-3YAT4",
				"oEgRlt4qQuLEZAHY0Z0oNWb5F8sQ", "oEgRlt_387gmyqLpUxvV0_vZeA5k",
				"oEgRlt0YFfyrJLYi6t3ehwza_nh4", "oEgRlt1dlNjR749p79O5Um9_6wSk",
				"oEgRltxOzpTMfg-vwOuDDlEx_4TU", "oEgRlt9_3wINBdQwH_QGNWyIApDM",
				"oEgRlt5edl2hvcYNiOyWMlkqStlU", "oEgRltzvYChnVmHP1vKa1ed1lu34",
				"oEgRltyFnJeg-LOgAiyXKZaHbGfk", "oEgRlt5Rxq5qSLDGDk2FV71uEU9s",
				"oEgRlt8fCaNAaWwPQnOGwaBmNwx4", "oEgRlt6bwC2TxAH3dPj2yl3-Wkhk",
				"oEgRlt2_0jYNrWRScdVw0PZutUlw", "oEgRltwO7Z-aLVRImzSmkfhygu_g",
				"oEgRlt9vvLcZ7UB3LC0kL7VjxJYE", "oEgRlt4l86Dfy1UnU3pgRbcIZikM",
				"oEgRlt9929BmvPbsfGscj7Q8cwh8",
				"oEgRlt0zPruB8E-4UwShdLHkbkVMod2SBjvsFK9XABwKcqlrxnQ5r8cc",
				"oEgRlt1MzJZk8t-xBCCqaRPVPMQY", "oEgRlt5iV6VasYs-NqcexV0u3PHo",
				"oEgRltzE3Lrr4wFXaIkMFyx7cndk", "oEgRlt7rulXSbNkwIX9rKF9bMe3I",
				"oEgRlt1YHdmBmuuWhtlnKlDkXRwQ", "oEgRlt453fXM2_fHRhDxX6WXhnBU",
				"oEgRlt7tIWL6j0UlEKpGmSt3p5-k", "oEgRlt0WvWYwvJpSMYANtnZ3ReIg",
				"oEgRltxBZ45tcTwIdWD2-sxAikkE", "oEgRlt2z00UrWLzkBTnzPLJ2GiaI",
				"oEgRltyBDtjtd27R4i4byJ4QZ650", "oEgRlt6bqax40icR1Y-zAAsfK5V0",
				"oEgRltwmCOiqSVr1KUEfIivftF5g", "oEgRltwrS5kDnPC43BBWbm2G0YZ8",
				"oEgRlt41zaSEyq6wTDCCyOZaJOf0", "oEgRlt7Jo6OCL79BFKBdvt3hEvMo",
				"oEgRlt6lx8gYZClbBQLXyBBfEipM",
				"oEgRlt3egiynnjQhRUw4EOHk8U0sod2SBjjh99Q2yX2uANLRM7ClJ27U",
				"oEgRltz_Z3nbDfGHNw2XJaFtTTCU", "oEgRlt0FlLmHmXk3m6KqqaYN_7cU",
				"oEgRlt-U_kPiOEOVuusKA0lK624Y", "oEgRltyC0zvkn0d-82XmfoeDk_1g",
				"oEgRlt4OITizuyGVzyNBYu0kP_eY", "oEgRlty2MFEm1HuaU8qNCw3c_cHM",
				"oEgRlt-MOhIMqYprhneECcnlQegE", "oEgRlt9HebDJAODvyAZ18sHyQ_n4",
				"oEgRlt1Chwpvly2wRUqP6u_uaIA8", "oEgRlt0N7_WCmhqhtpSmupzqbZmI",
				"oEgRlt7b0BKz6tY72eqxxc5yosC4", "oEgRlt1965fEXQdexjgSoz-VcMoM",
				"oEgRlt7DZ2rH-k9eKpTzZncqZ5KM", "oEgRltwneIFB8h4w3KxYa-_kVsgo",
				"oEgRlt0_BDNn5gZ9Kh22dZRfeR_Y", "oEgRlt7Fd5vv4YWf05gs2sYqMcEM",
				"oEgRlt2COc_h83HyMjCM-vAUA5CU", "oEgRlt4PrRnJL6e_rJCiP7dchoZM",
				"oEgRlt8GrKJW-88-4a9lveEer70I", "oEgRlt7BxkVWw80hSDZ3__fE-bsw",
				"oEgRltwcoBWE7Y0C4Y965lguGAlI", "oEgRltx6_cSIEx4aFUo9c0Mfkec0",
				"oEgRlt09I6SsTERHVzVB1j0izt7A", "oEgRlt5IcxJ_mozbKG_Cht14abD4",
				"oEgRltxNc8_xvvXr4hzmy2ltsFg8", "oEgRlt-H0C-XewhkVOj4zPcUnptw",
				"oEgRlt8vCVAAvvr0Y5GqRQANb5asod2SBjk-sho-nvkzwFBr_vys5CYY",
				"oEgRlt6hUIqEeRTqVpogWsP2cHHs", "oEgRlt3xMnwcNJX56hDTV6GeF5Z4",
				"oEgRlt6Js9oXG5pg1dLOx1irXOi4", "oEgRlt45h7m2SmCCqBhFzftxEN68",
				"oEgRlt4B5MAovG27oeYsvtOxCLRY" };

		for (String str : arry) {
			String acc = "{\"touser\":\""
					+ str.trim()
					+ "\",\"msgtype\":\"text\",\"text\":{\"content\":\"最后一小时，199元集客回家礼盒还在等着你领回家！文具控们，说好的不离不弃呢？速度集满9枚章，领取免费礼盒！只剩下60分钟了，快快来兑奖~\"}}\"";

			String ret = doPost(
					"https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=IktBEmIerQq0o8Pq8DJ48vloT8JSHUbB90YEpl6qxOuG_dOoxwiC_1CW_2R5SQ5LLP0sx9uqP5vtAQ2XbqTeaA8TRYMlH7TE9FHOyx9yfO0",
					acc);

			System.err.println("rest: " + ret);
			if (!ret.contains("ok")) {
				a += 1;
				System.err.println("a: " + a);
			}
		}
		System.err.println("rest:完成,未送达:" + a);
	}

	// 字符串转换
	public static String listToString(List list, char separator) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			sb.append(list.get(i));
			if (i < list.size() - 1) {
				sb.append(separator);
			}
		}
		return sb.toString();
	}
}
