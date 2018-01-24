/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package roc.gc.push.websocket;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Static utility class containing methods used for websocket encoding
 * and decoding.
 *
 * @author DHRUV CHOPRA
 */
class WebSocketUtils {
    
    static final String SessionAttribute = "isWEB";
    // Construct a successful websocket handshake response using the key param
    // (See RFC 6455).
    static WebSocketHandShakeResponse buildWSHandshakeResponse(String key){
        String response = "HTTP/1.1 101 Web Socket Protocol Handshake\r\n";
        response += "Upgrade: websocket\r\n";
        response += "Connection: Upgrade\r\n";
        response += "Sec-WebSocket-Accept: " + key + "\r\n";
        response += "\r\n";        
        return new WebSocketHandShakeResponse(response);
    }
    
    // Parse the string as a websocket request and return the value from
    // Sec-WebSocket-Key header (See RFC 6455). Return empty string if not found.
    static String getClientWSRequestKey(String WSRequest) {
        String[] headers = WSRequest.split("\r\n");
        String socketKey = "";
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].contains("Sec-WebSocket-Key")) {
                socketKey = (headers[i].split(":")[1]).trim();
                break;
            }
        }
        return socketKey;
    }    
    
    // 
    // Builds the challenge response to be used in WebSocket handshake.
    // First append the challenge with "258EAFA5-E914-47DA-95CA-C5AB0DC85B11" and then
    // make a SHA1 hash and finally Base64 encode it. (See RFC 6455)
    static String getWebSocketKeyChallengeResponse(String challenge) throws NoSuchAlgorithmException, UnsupportedEncodingException {

        challenge += "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
        MessageDigest cript = MessageDigest.getInstance("SHA-1");
        cript.reset();
        cript.update(challenge.getBytes("utf8"));
        byte[] hashedVal = cript.digest();        
        return Base64.encodeBytes(hashedVal);
    }
    
    // 对传入数据进行无掩码转换
    public static byte[] encode(String msg) throws UnsupportedEncodingException {
        // 掩码开始位置
        int masking_key_startIndex = 2;

        byte[] msgByte = msg.getBytes("UTF-8");

        // 计算掩码开始位置
        if (msgByte.length <= 125) {
            masking_key_startIndex = 2;
        } else if (msgByte.length > 65536) {
            masking_key_startIndex = 10;
        } else if (msgByte.length > 125) {
            masking_key_startIndex = 4;
        }

        // 创建返回数据
        byte[] result = new byte[msgByte.length + masking_key_startIndex];

        // 开始计算ws-frame
        // frame-fin + frame-rsv1 + frame-rsv2 + frame-rsv3 + frame-opcode
        result[0] = (byte) 0x81; // 129

        // frame-masked+frame-payload-length
        // 从第9个字节开始是 1111101=125,掩码是第3-第6个数据
        // 从第9个字节开始是 1111110>=126,掩码是第5-第8个数据
        if (msgByte.length <= 125) {
            result[1] = (byte) (msgByte.length);
        } else if (msgByte.length > 65536) {
            result[1] = 0x7F; // 127
        } else if (msgByte.length > 125) {
            result[1] = 0x7E; // 126
            result[2] = (byte) (msgByte.length >> 8);
            result[3] = (byte) (msgByte.length % 256);
        }

        // 将数据编码放到最后
        for (int i = 0; i < msgByte.length; i++) {
            result[i + masking_key_startIndex] = msgByte[i];
        }

        return result;
    }
}
