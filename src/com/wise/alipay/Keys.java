/*
 * Copyright (C) 2010 The MobileSecurePay Project
 * All right reserved.
 * author: shiqun.shi@alipay.com
 * 
 *  提示：如何获取安全校验码和合作身份者id
 *  1.用您的签约支付宝账号登录支付宝网站(www.alipay.com)
 *  2.点击“商家服务”(https://b.alipay.com/order/myorder.htm)
 *  3.点击“查询合作者身份(pid)”、“查询安全校验码(key)”
 */

package com.wise.alipay;

//
// 请参考 Android平台安全支付服务(msp)应用开发接口(4.2 RSA算法签名)部分，并使用压缩包中的openssl RSA密钥生成工具，生成一套RSA公私钥。
// 这里签名时，只需要使用生成的RSA私钥。
// Note: 为安全起见，使用RSA私钥进行签名的操作过程，应该尽量放到商家服务器端去进行。
public final class Keys {

	//合作身份者id，以2088开头的16位纯数字
	public static final String DEFAULT_PARTNER = "2088301535733532";

	//收款支付宝账号
	public static final String DEFAULT_SELLER = "thomas@wisegps.cn";

	//商户私钥，自助生成
	public static final String PRIVATA = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAPNxfVb1Yu53jax5IIeIpAOF1AiQfKkFCcpzSwBS+vSVrK/0LZ2jp4bqAXuYQZKHt1pxDjCVpvfTGY2AiYL4g7P/nx0mMXL4mSZS2QE82wxdQzuUS5cIlldz4HGMqugpNM6exIh39gq15WU4Yv7/gcld24iAGRxcxsrBAN/LoGVvAgMBAAECgYEAz/JYe2g+qq5KwVHyPHO2jh/J38r0ATiBhYJ4RX2cSkJz8RTlxiseFftbauTC1lTBhSrfURb0OesDf8CVNd/sNrGS/UIUM632Lge3d5He+rR5h5GBrd+2u748AL7I0ZFlpf3+ax7JVFNqPC32NPOO42essjgCDt9Fqw1aKiF9t+kCQQD9B+vWxMJpyCQheoLjrYgzJsPsjMdCgI1wQuijpWXmt+6R51GVwLjW9WvvgvsrpcSIivhuoZBQJgrEWaL6NxuFAkEA9kzEpGgfVBPAUc2Quu1kLKcXr8BjNsiO/HplaBadXzbaGQ3GO12ZHU5C5kxSYiAm4Y+IfymQiK5nRDXUfK8NYwJACO8gqqKj4qxIak4zFOrppjS2MkH+PyDe3ZnfMPgEExNnfwtV3B3D6QMdXoWXbMEboV6WywE1YT8Wnjwz/vW3KQJAaBzlVrXo4EqFFk816b5lhPaQ7ar+hW7b+l/ms8wzc8clBEgtTnTvP1MQtnb2sCH1LY3V3iL8xLosTsoOazI4DwJBAJraDRNYllWclpJaAwOqo8WwLTMJK/2CXm7f5eFWy4WgFqj+/e7tpHw8Udx6yppBvmW25Xxcva4FxrscYQcB/2g=";
	public static final String PRIVATE = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAK2Xfan7GqXxA7guJBTV6Rn4yyBYHGgzJ30dqHSaxyG2OKzu3LUM5rbqJKU03fQWzn6AItv8hcVFGP2Q9RAPL9G9nuBR+56dGnQJ5398HiOZO2u/kvsP8sT0CXeBCFNj9K9Gak4sjJfHOkNF5azxlcsnGfNV6abZaMIntk5V0BqXAgMBAAECgYA+D3jnulQum1nfEO/pWwh5No84Qwf04MTvYBkHhwZGMSVV/hd9ytz47ACp55qqVzT/2MOdrkwM2MU0cgyK70MCvC+mLHi7OAx0SHrMxBm5f9/+zDAUqGECiVZfPQwSVTxrWZKgvU1QLMvrXs8Je9LMvVwY4Up0CLu0OIrXH90XcQJBAN+VcqFQUOocFtsu//BjVyGAcsNc2iyXi6EnHkUST/hca+Zp5AMyx7m02PdTHon35pm0rK0G72rfhQJFnT0x++UCQQDGwokdC/tJGc6QY4XUVvAc+7EcY+v07eMA1zKVdCxx1hlPMopfZBxhvS46F6fU2nuuZ8O6LZcInqTKBTQuOizLAkAc9tVUxHzW9zCW0G3jjFr7QhKb8GlrIW67P8ASHp8xg3eO7+TT7T4mdqEs2R25rd23x8oe2Ckn5TDr7GzEQrdpAkAcyYg6YMXgbJBycTes7XqReBLK3d4K93ltYb29z7mwMyYvRk6sSj+iGFhdqZdxSMOpGvZKPNgnM3MCn4ZTO7HJAkAEWIo6E3890i3aBkCb4zWboKEvWM4a6Il4Zr5YyV8Hnezr9gRCQqZTFtA8w0foPHl3wPwBtxiajl4cGwxHyXf7";

	public static final String PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";

}
