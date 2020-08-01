package com.betfair.aping.api2;


import com.google.common.base.Optional;
import com.google.common.reflect.TypeToken;
import com.jbetfairng.entities.*;
import com.jbetfairng.enums.*;
import com.google.gson.Gson;
import com.betfair.aping.util.Constants;
import com.betfair.aping.util.Helpers;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.util.HashMap;
import java.util.List;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Set;


public class BetfairClient {
	
	private Network networkClient;
	 private Exchange exchange;

    private static String LIST_COMPETITIONS_METHOD = "SportsAPING/v1.0/listCompetitions";
    private static String LIST_COUNTRIES_METHOD = "SportsAPING/v1.0/listCountries";
    private static String LIST_CURRENT_ORDERS_METHOD = "SportsAPING/v1.0/listCurrentOrders";
    private static String LIST_CLEARED_ORDERS_METHOD = "SportsAPING/v1.0/listClearedOrders";
    private static String LIST_EVENT_TYPES_METHOD = "SportsAPING/v1.0/listEventTypes";
    private static String LIST_EVENTS_METHOD = "SportsAPING/v1.0/listEvents";
    private static String LIST_MARKET_CATALOGUE_METHOD = "SportsAPING/v1.0/listMarketCatalogue";
    private static String LIST_MARKET_BOOK_METHOD = "SportsAPING/v1.0/listMarketBook";
    private static String LIST_MARKET_PROFIT_AND_LOSS = "SportsAPING/v1.0/listMarketProfitAndLoss";
    private static String LIST_MARKET_TYPES = "SportsAPING/v1.0/listMarketTypes";
    private static String LIST_TIME_RANGES = "SportsAPING/v1.0/listTimeRanges";
    private static String LIST_VENUES = "SportsAPING/v1.0/listVenues";
    private static String PLACE_ORDERS_METHOD = "SportsAPING/v1.0/placeOrders";
    private static String CANCEL_ORDERS_METHOD = "SportsAPING/v1.0/cancelOrders";
    private static String REPLACE_ORDERS_METHOD = "SportsAPING/v1.0/replaceOrders";
    private static String UPDATE_ORDERS_METHOD = "SportsAPING/v1.0/updateOrders";

    private static String GET_ACCOUNT_DETAILS = "AccountAPING/v1.0/getAccountDetails";
    private static String GET_ACCOUNT_FUNDS = "AccountAPING/v1.0/getAccountFunds";
    private static String GET_ACCOUNT_STATEMENT = "AccountAPING/v1.0/getAccountStatement";
    private static String LIST_CURRENCY_RATES = "AccountAPING/v1.0/listCurrencyRates";
    private static String TRANSFER_FUNDS = "AccountAPING/v1.0/transferFunds";

    private static String FILTER = "filter";
    private static String BET_IDS = "betIds";
    private static String RUNNER_IDS = "runnerIds";
    private static String SIDE = "side";
    private static String SETTLED_DATE_RANGE = "settledDateRange";
    private static String EVENT_TYPE_IDS = "eventTypeIds";
    private static String EVENT_IDS = "eventIds";
    private static String BET_STATUS = "betStatus";
    private static String PLACED_DATE_RANGE = "placedDateRange";
    private static String DATE_RANGE = "dateRange";
    private static String ORDER_BY = "orderBy";
    private static String GROUP_BY = "groupBy";
    private static String SORT_DIR = "sortDir";
    private static String FROM_RECORD = "fromRecord";
    private static String RECORD_COUNT = "recordCount";
    private static String GRANULARITY = "granularity";
    private static String MARKET_PROJECTION = "marketProjection";
    private static String MATCH_PROJECTION = "matchProjection";
    private static String ORDER_PROJECTION = "orderProjection";
    private static String PRICE_PROJECTION = "priceProjection";
    private static String SORT = "sort";
    private static String MAX_RESULTS = "maxResults";
    private static String MARKET_IDS = "marketIds";
    private static String MARKET_ID = "marketId";
    private static String INSTRUCTIONS = "instructions";
    private static String CUSTOMER_REFERENCE = "customerRef";
    private static String MARKET_VERSION = "marketVersion";
    private static String INCLUDE_SETTLED_BETS = "includeSettledBets";
    private static String INCLUDE_BSP_BETS = "includeBspBets";
    private static String INCLUDE_ITEM_DESCRIPTION = "includeItemDescription";
    private static String NET_OF_COMMISSION = "netOfCommission";
    private static String FROM_CURRENCY = "fromCurrency";
    private static String FROM = "from";
    private static String TO = "to";
    private static String AMOUNT = "amount";
    private static String WALLET = "wallet";
    private static String ITEM_DATE_RANGE = "itemDateRange";
    private static String INCLUDE_ITEM = "includeItem";



    /**
     * Static defined identity endpoints
     */
    private static HashMap<Exchange, String> identityEndpoints = new HashMap<>();

    static {
        identityEndpoints.put(Exchange.RO, "https://identitysso-cert.betfair.ro/api/certlogin");
        identityEndpoints.put(Exchange.UK, "https://identitysso-cert.betfair.com/api/certlogin");
        identityEndpoints.put(Exchange.AUS, "https://identitysso-cert.betfair.com/api/certlogin");
        identityEndpoints.put(Exchange.IT, "https://identitysso-cert.betfair.it/api/certlogin");
        identityEndpoints.put(Exchange.ES, "https://identitysso-cert.betfair.es/api/certlogin");
    }

    public BetfairClient(String appKey, String sessionToken) {
		this.networkClient = new Network(appKey, sessionToken, false);
	}

	public BetfairServerResponse<KeepAliveResponse> keepAlive() {
        return networkClient.keepAliveSynchronous();
    }

    public BetfairServerResponse<List<CompetitionResult>> listCompetitions(MarketFilter marketFilter) {
        HashMap<String, Object> args = new HashMap<>();
        args.put(FILTER, marketFilter);
        return networkClient.Invoke(new TypeToken<List<CompetitionResult>>() {
                                    }, this.exchange,
                Endpoint.Betting, LIST_COMPETITIONS_METHOD, args);
    }

    public BetfairServerResponse<List<CountryCodeResult>> listCountries(MarketFilter marketFilter) {
        HashMap<String, Object> args = new HashMap<>();
        args.put(FILTER, marketFilter);
        return networkClient.Invoke(
                new TypeToken<List<CountryCodeResult>>() {
                },
                this.exchange,
                Endpoint.Betting,
                LIST_COUNTRIES_METHOD,
                args);
    }

    public BetfairServerResponse<List<EventResult>> listEvents(MarketFilter marketFilter) {
        HashMap<String, Object> args = new HashMap<>();
        args.put(FILTER, marketFilter);
        return networkClient.Invoke(
                new TypeToken<List<EventResult>>() {
                },
                this.exchange,
                Endpoint.Betting,
                LIST_EVENTS_METHOD,
                args);
    }

    public BetfairServerResponse<List<EventTypeResult>> listEventTypes(MarketFilter marketFilter) {
        HashMap<String, Object> args = new HashMap<>();
        args.put(FILTER, marketFilter);
        return networkClient.Invoke(
                new TypeToken<List<EventTypeResult>>() {
                },
                this.exchange,
                Endpoint.Betting,
                LIST_EVENT_TYPES_METHOD,
                args);
    }

    public BetfairServerResponse<List<MarketBook>> listMarketBook(
            List<String> marketIds,
            PriceProjection priceProjection,
            OrderProjection orderProjection,
            MatchProjection matchProjection) {
        HashMap<String, Object> args = new HashMap<>();
        args.put(MARKET_IDS, marketIds);
        args.put(PRICE_PROJECTION, priceProjection);
        args.put(ORDER_PROJECTION, orderProjection);
        args.put(MATCH_PROJECTION, matchProjection);
        return networkClient.Invoke(
                new TypeToken<List<MarketBook>>() {
                },
                this.exchange,
                Endpoint.Betting,
                LIST_MARKET_BOOK_METHOD,
                args);
    }

    public BetfairServerResponse<List<MarketCatalogue>> listMarketCatalogue(
            MarketFilter marketFilter,
            Set<MarketProjection> marketProjections,
            MarketSort sort,
            int maxResult) {
        HashMap<String, Object> args = new HashMap<>();
        args.put(FILTER, marketFilter);
        args.put(MARKET_PROJECTION, marketProjections);
        args.put(SORT, sort);
        args.put(MAX_RESULTS, maxResult);
        return networkClient.Invoke(
                new TypeToken<List<MarketCatalogue>>() {
                },
                this.exchange,
                Endpoint.Betting,
                LIST_MARKET_CATALOGUE_METHOD,
                args);
    }

    public BetfairServerResponse<List<MarketTypeResult>> listMarketTypes( MarketFilter marketFilter) {
        HashMap<String, Object> args = new HashMap<>();
        args.put(FILTER, marketFilter);
        return networkClient.Invoke(
                new TypeToken<List<MarketTypeResult>>() {
                },
                this.exchange,
                Endpoint.Betting,
                LIST_MARKET_TYPES,
                args);
    }

    public BetfairServerResponse<List<MarketProfitAndLoss>> listMarketProfitAndLoss(
            List<String> marketIds,
            Boolean includeSettledBets,
            Boolean includeBsbBets,
            Boolean netOfComission) {
        HashMap<String, Object> args = new HashMap<>();
        args.put(MARKET_IDS, marketIds);
        args.put(INCLUDE_SETTLED_BETS, includeSettledBets);
        args.put(INCLUDE_BSP_BETS, includeBsbBets);
        args.put(NET_OF_COMMISSION, netOfComission);
        return networkClient.Invoke(
                new TypeToken<List<MarketProfitAndLoss>>() {
                },
                this.exchange,
                Endpoint.Betting,
                LIST_MARKET_PROFIT_AND_LOSS,
                args);
    }

    public BetfairServerResponse<List<TimeRangeResult>> listTimeRanges(
            MarketFilter marketFilter,
            TimeGranularity timeGranularity) {
        HashMap<String, Object> args = new HashMap<>();
        args.put(FILTER, marketFilter);
        args.put(GRANULARITY, timeGranularity);
        return networkClient.Invoke(
                new TypeToken<List<TimeRangeResult>>() {
                },
                this.exchange,
                Endpoint.Betting,
                LIST_TIME_RANGES,
                args);
    }

    public BetfairServerResponse<List<VenueResult>> listVenues(MarketFilter marketFilter) {
        HashMap<String, Object> args = new HashMap<>();
        args.put(FILTER, marketFilter);
        return networkClient.Invoke(
                new TypeToken<List<VenueResult>>() {
                },
                this.exchange,
                Endpoint.Betting,
                LIST_VENUES,
                args);
    }

    public BetfairServerResponse<CurrentOrderSummaryReport> listCurrentOrders(
            Set<String> betIds,
            Set<String> marketIds,
            OrderProjection orderProjection,
            TimeRange placedDateRange,
            TimeRange dateRange,
            OrderBy orderBy,
            SortDir sortDir,
            Optional<Integer> fromRecord,
            Optional<Integer> recordCount) {
        HashMap<String, Object> args = new HashMap<>();
        args.put(BET_IDS, betIds);
        args.put(MARKET_IDS, marketIds);
        args.put(ORDER_PROJECTION, orderProjection);
        args.put(PLACED_DATE_RANGE, placedDateRange);
        args.put(DATE_RANGE, dateRange);
        args.put(ORDER_BY, orderBy);
        args.put(SORT_DIR, sortDir);
        args.put(FROM_RECORD, fromRecord);
        args.put(RECORD_COUNT, recordCount);
        return networkClient.Invoke(
                new TypeToken<CurrentOrderSummaryReport>() {
                },
                this.exchange,
                Endpoint.Betting,
                LIST_CURRENT_ORDERS_METHOD,
                args);
    }

    public BetfairServerResponse<ClearedOrderSummaryReport> listClearedOrders(
            BetStatus betStatus,
            Set<String> eventTypeIds,
            Set<String> eventIds,
            Set<String> marketIds,
            Set<String> runnerIds,
            Set<String> betIds,
            Side side,
            TimeRange settledDateRange,
            GroupBy groupBy,
            Boolean includeItemDescription,
            Integer fromRecord,
            Integer recordCount) {
        HashMap<String, Object> args = new HashMap<>();
        args.put(BET_STATUS, betStatus);
        args.put(EVENT_TYPE_IDS, eventTypeIds);
        args.put(EVENT_IDS, eventIds);
        args.put(MARKET_IDS, marketIds);
        args.put(BET_IDS, betIds);
        args.put(SIDE, side);
        args.put(DATE_RANGE, settledDateRange);
        args.put(GROUP_BY, groupBy);
        args.put(INCLUDE_ITEM_DESCRIPTION, includeItemDescription);
        args.put(FROM_RECORD, fromRecord);
        args.put(RECORD_COUNT, recordCount);

        return networkClient.Invoke(
                new TypeToken<ClearedOrderSummaryReport>() {
                },
                this.exchange,
                Endpoint.Betting,
                LIST_CLEARED_ORDERS_METHOD,
                args);
    }

    public BetfairServerResponse<PlaceExecutionReport> placeOrders(
            String marketId,
            List<PlaceInstruction> placeInstructions,
            String customerRef, long marketVersion) {
        HashMap<String, Object> args = new HashMap<>();
        args.put(MARKET_ID, marketId);
        args.put(INSTRUCTIONS, placeInstructions);
        args.put(CUSTOMER_REFERENCE, customerRef);
        args.put(MARKET_VERSION, new MarketVersion(marketVersion));

        return networkClient.Invoke(
                new TypeToken<PlaceExecutionReport>() {
                },
                this.exchange,
                Endpoint.Betting,
                PLACE_ORDERS_METHOD,
                args);
    }

    public BetfairServerResponse<CancelExecutionReport> cancelOrders(
            String marketId,
            List<CancelInstruction> instructions,
            String customerRef) {
        HashMap<String, Object> args = new HashMap<>();
        args.put(MARKET_ID, marketId);
        args.put(INSTRUCTIONS, instructions);
        args.put(CUSTOMER_REFERENCE, customerRef);

        return networkClient.Invoke(
                new TypeToken<CancelExecutionReport>() {
                },
                this.exchange,
                Endpoint.Betting,
                CANCEL_ORDERS_METHOD,
                args);
    }

    public BetfairServerResponse<ReplaceExecutionReport> replaceOrders(
            String marketId,
            List<ReplaceInstruction> instructions,
            String customerRef) {
        HashMap<String, Object> args = new HashMap<>();
        args.put(MARKET_ID, marketId);
        args.put(INSTRUCTIONS, instructions);
        args.put(CUSTOMER_REFERENCE, customerRef);

        return networkClient.Invoke(
                new TypeToken<ReplaceExecutionReport>() {
                },
                this.exchange,
                Endpoint.Betting,
                REPLACE_ORDERS_METHOD,
                args);
    }

    public BetfairServerResponse<UpdateExecutionReport> updateOrders(
            String marketId,
            List<UpdateInstruction> instructions,
            String customerRef) {
        HashMap<String, Object> args = new HashMap<>();
        args.put(MARKET_ID, marketId);
        args.put(INSTRUCTIONS, instructions);
        args.put(CUSTOMER_REFERENCE, customerRef);

        return networkClient.Invoke(
                new TypeToken<UpdateExecutionReport>() {
                },
                this.exchange,
                Endpoint.Betting,
                UPDATE_ORDERS_METHOD,
                args);
    }

    // Account API's
    public BetfairServerResponse<AccountDetailsResponse> getAccountDetails() {
        HashMap<String, Object> args = new HashMap<>();
        return networkClient.Invoke(
                new TypeToken<AccountDetailsResponse>() {
                },
                this.exchange,
                Endpoint.Account,
                GET_ACCOUNT_DETAILS,
                args);
    }

    public BetfairServerResponse<AccountFundsResponse> getAccountFunds(Wallet wallet) {
        HashMap<String, Object> args = new HashMap<>();
        args.put(WALLET, wallet);
        return networkClient.Invoke(
                new TypeToken<AccountFundsResponse>() {
                },
                this.exchange,
                Endpoint.Account,
                GET_ACCOUNT_FUNDS,
                args);
    }

    public BetfairServerResponse<AccountStatementReport> getAccountStatement(
            Integer fromRecord,
            Integer recordCount,
            TimeRange itemDateRange,
            IncludeItem includeItem,
            Wallet wallet) {
        HashMap<String, Object> args = new HashMap<>();
        args.put(FROM_RECORD, fromRecord);
        args.put(RECORD_COUNT, recordCount);
        args.put(ITEM_DATE_RANGE, itemDateRange);
        args.put(INCLUDE_ITEM, includeItem);
        args.put(WALLET, wallet);
        return networkClient.Invoke(
                new TypeToken<AccountStatementReport>() {
                },
                this.exchange,
                Endpoint.Account,
                GET_ACCOUNT_STATEMENT,
                args);
    }

    public BetfairServerResponse<List<CurrencyRate>> listCurrencyRates(String fromCurrency) {
        HashMap<String, Object> args = new HashMap<>();
        args.put(FROM_CURRENCY, fromCurrency);
        return networkClient.Invoke(
                new TypeToken<List<CurrencyRate>>() {
                },
                this.exchange,
                Endpoint.Account,
                LIST_CURRENCY_RATES,
                args);
    }

    public BetfairServerResponse<TransferResponse> transferFunds(Wallet from, Wallet to, double amount) {
        HashMap<String, Object> args = new HashMap<>();
        args.put(FROM, from);
        args.put(TO, to);
        args.put(AMOUNT, amount);
        return networkClient.Invoke(
                new TypeToken<TransferResponse>() {
                },
                this.exchange,
                Endpoint.Account,
                TRANSFER_FUNDS,
                args);
    }

}
