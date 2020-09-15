package aug.common.gateway.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.DefaultClaims;
import io.jsonwebtoken.security.Keys;
import org.joda.time.DateTime;

import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Jwt工具
 *
 * @author guoxiaoyong
 * @date 2018/10/10
 */
public class JwtUtils {

    private static final byte[] SIGN_KEY_SECRET = "c1e6aa0b60454edcb891211dccf01e05a5a7b9536c9741edb1de993763fbd538".getBytes(StandardCharsets.UTF_8);

    /**
     * 签发token
     *
     * @return
     */
    public static String sign(String uid) {

        Date expireTime = DateTime.now().plusMinutes(60).toDate();

        Claims claims = new DefaultClaims();
        claims.put("uid", uid);
        claims.put("expireAt", expireTime);

        return Jwts.builder()
                .setClaims(claims)
                .signWith(Keys.hmacShaKeyFor(SIGN_KEY_SECRET))
                .setExpiration(expireTime)
                .compact();
    }

    public static boolean verifyToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SIGN_KEY_SECRET)
                    .parseClaimsJws(token)
                    .getBody();
            return claims != null;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getUid(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SIGN_KEY_SECRET)
                    .parseClaimsJws(token)
                    .getBody();
            if (claims == null) {
                return null;
            }
            return claims.get("uid", String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public static void main(String[] args) {
        String token = JwtUtils.sign("000001");
        System.out.println(token);
        JwtUtils.verifyToken(token);

    }
}
