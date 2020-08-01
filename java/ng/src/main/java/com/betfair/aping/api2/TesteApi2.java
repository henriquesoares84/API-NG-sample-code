package com.betfair.aping.api2;

import com.betfair.aping.util.HttpClientSSO;
import com.jbetfairng.entities.AccountDetailsResponse;
import com.jbetfairng.entities.AccountFundsResponse;
import com.jbetfairng.enums.Wallet;

public class TesteApi2 {

	public static void main(String[] args) {
		
		try {
			String appKey = "NB7RwYHUuDcafODs";
//			String sessionToken = HttpClientSSO.login();
			String sessionToken = "YI0l4Zrk170+7u3NB5bq1X/ecuyJp10O4EOqqOOsCos=";
			
			BetfairClient client = new BetfairClient(appKey, sessionToken);
			
			AccountDetailsResponse details = client.getAccountDetails().getResponse();
			System.out.println(details);
			
			AccountFundsResponse balance = client.getAccountFunds(Wallet.UK).getResponse();
			System.out.println(balance);
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
