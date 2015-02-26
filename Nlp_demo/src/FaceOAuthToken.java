import facebook4j.Facebook;
import facebook4j.FacebookFactory;
import facebook4j.Post;
import facebook4j.ResponseList;
import facebook4j.conf.ConfigurationBuilder;


public class FaceOAuthToken {
	private String OAuthAppId="881677971854230";
	private String OAuthPermissions="email,publish_stream,...";
	private String OAuthSecret="27d0624c558d59dc098145b332314ec8";
	private String OAuthAccessToken="CAACEdEose0cBAJC1v6pWGBMRH3pXmOGMqI5xr2akjwRn209uCH881TEZB8IfZAylNMQu2bvfS6tcwLxPF82wzmUk8ZBZCjwqOygvzJZAJLNPKsMHqB5OzdNWKueY2ELjTjAnKzEUsOlw56WvFv4hmk05hTgjlkRBd0gylFCrCfl4DKaQzb6TLR0lZA0LZBBwPkZD";
	public Facebook facebook;
	
	public FaceOAuthToken(String id, String permiss, String secret,String accessToken)
	{
		changeToken(id, permiss, secret, accessToken);
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		  .setOAuthAppId(this.OAuthAppId)
		  .setOAuthAppSecret(this.OAuthSecret)
		  .setOAuthAccessToken(this.OAuthAccessToken)
		  .setOAuthPermissions(this.OAuthPermissions);
		FacebookFactory faceFac = new FacebookFactory(cb.build());
		this.facebook = faceFac.getInstance();
		
		
	}
	public FaceOAuthToken()
	{
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		  .setOAuthAppId(this.OAuthAppId)
		  .setOAuthAppSecret(this.OAuthSecret)
		  .setOAuthAccessToken(this.OAuthAccessToken)
		  .setOAuthPermissions(this.OAuthPermissions);
		FacebookFactory faceFac = new FacebookFactory(cb.build());
		this.facebook = faceFac.getInstance();
	}
	public void changeToken(String appId,String permiss, String secret, String accessToken)
	{
		this.OAuthAccessToken=accessToken;
		this.OAuthAppId=appId;
		this.OAuthPermissions=permiss;
		this.OAuthSecret=secret;

	}

}
