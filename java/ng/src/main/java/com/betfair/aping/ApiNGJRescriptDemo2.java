package com.betfair.aping;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.betfair.aping.api.ApiNgOperations;
import com.betfair.aping.api.ApiNgRescriptOperations;
import com.betfair.aping.entities.Event;
import com.betfair.aping.entities.ExchangePrices;
import com.betfair.aping.entities.LimitOrder;
import com.betfair.aping.entities.MarketBook;
import com.betfair.aping.entities.MarketCatalogue;
import com.betfair.aping.entities.MarketFilter;
import com.betfair.aping.entities.PlaceExecutionReport;
import com.betfair.aping.entities.PlaceInstruction;
import com.betfair.aping.entities.PlacedOrder;
import com.betfair.aping.entities.PriceProjection;
import com.betfair.aping.entities.PriceSize;
import com.betfair.aping.entities.Runner;
import com.betfair.aping.entities.RunnerCatalog;
import com.betfair.aping.entities.TimeRange;
import com.betfair.aping.enums.ExecutionReportStatus;
import com.betfair.aping.enums.MarketProjection;
import com.betfair.aping.enums.MarketSort;
import com.betfair.aping.enums.MatchProjection;
import com.betfair.aping.enums.OrderProjection;
import com.betfair.aping.enums.OrderType;
import com.betfair.aping.enums.PersistenceType;
import com.betfair.aping.enums.PriceData;
import com.betfair.aping.enums.Side;
import com.betfair.aping.exceptions.APINGException;

/**
 * This is a demonstration class to show a quick demo of the new Betfair API-NG.
 * When you execute the class will: <li>find a market (next horse race in the
 * UK)</li> <li>get prices and runners on this market</li> <li>place a bet on 1
 * runner</li> <li>handle the error</li>
 * 
 */
public class ApiNGJRescriptDemo2 {

	private ApiNgOperations rescriptOperations = ApiNgRescriptOperations.getInstance();
	private String applicationKey;
	private String sessionToken;
	
	final Map<String, MarketCatalogue> markets = new LinkedHashMap<String, MarketCatalogue>();
	
	DecimalFormat decimalFormat = new DecimalFormat("0.##");
	
	Integer customerRef = 12;
	
	Double piorPorcentagem = 0d;
    Double melhorPorcentagem = -5d;
    List<String> encontradosMaiorQueZero = new ArrayList<String>();
    Double piorPorcentagemGeral = 0d;
    Double melhorPorcentagemGeral = -5d;
    int encontradosMaiorQueZeroGeral = 0;
    int encontradosMaiorQueZeroGeralDoubleChanceXMatchOdds = 0;
    
    final List<Boolean> mercadosProcessados = new ArrayList<Boolean>();
    
    Set<Entry<String, List<MarketCatalogue>>> mercadosPorEventoChaveEValor;
    
    final Map<String, Event> eventos = new LinkedHashMap<String, Event>();

    public void startTesteListCurrentOrders(String appKey, String ssoid) {
    	 this.applicationKey = appKey;
         this.sessionToken = ssoid;
    	MarketFilter marketFilter = new MarketFilter();
		try {
			Set<String> betIds = new HashSet<String>();
			betIds.add("206952445635");
//			marketFilter.setBetIds(betIds);
			List<PlacedOrder> r = rescriptOperations.listCurrentOrders(marketFilter, applicationKey, sessionToken);
			for (PlacedOrder placedOrder : r) {
				System.out.println(placedOrder);
			}
		} catch (APINGException e) {
			e.printStackTrace();
		}
    }
    public void start(String appKey, String ssoid) {
    	this.applicationKey = appKey;
        this.sessionToken = ssoid;
    	adicionaApostaNoCSV(new Date() + " - Iniciando aplicação");
    	
    	try {
    		System.out.println("buscando mercados");
			buscaMercados();
		} catch (APINGException e1) {
			e1.printStackTrace();
		}
    	
    	new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					Thread.sleep(10 * 1000 * 60); //10 minutos
					buscaMercados();
				} catch (APINGException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} 
			}
		}).start();
    	
		processaMercados(appKey, ssoid);
    		
    }
    
    
    private void buscaMercados() throws APINGException {
    	
    	MarketFilter marketFilter;
        marketFilter = new MarketFilter();
        Set<String> eventTypeIds = new HashSet<String>();
        eventTypeIds.add("1"); //Futebol
        
        Date dataInicio = new Date();
        Calendar calFim = Calendar.getInstance();
		calFim.add(Calendar.DAY_OF_MONTH, 3);
		
		
		Date ultimaDataRecebida = null;
		
        
        TimeRange time = new TimeRange();
        time.setFrom(dataInicio);
        
        System.out.println("Filtro de data: " + dataInicio);
        
        marketFilter = new MarketFilter();
        marketFilter.setEventTypeIds(eventTypeIds);
        Set<String> eventIds = new HashSet<String>();
//        String idEventoSelecionado = "29929982";
//        eventIds.add(idEventoSelecionado);
//		marketFilter.setEventIds(eventIds );
        marketFilter.setMarketStartTime(time);
        
        Set<String> marketIdsTemp = new HashSet<String>();
//        marketIdsTemp.add("1.171661203");
		marketFilter.setMarketIds(marketIdsTemp );
//        marketFilter.setMarketCountries(countries);
//        marketFilter.setMarketTypeCodes(typesCode);

        Set<MarketProjection> marketProjection = new HashSet<MarketProjection>();
//        marketProjection.add(MarketProjection.COMPETITION);
        marketProjection.add(MarketProjection.EVENT);
        marketProjection.add(MarketProjection.EVENT_TYPE);
        marketProjection.add(MarketProjection.MARKET_DESCRIPTION);
        marketProjection.add(MarketProjection.RUNNER_DESCRIPTION);
//        marketProjection.add(MarketProjection.RUNNER_METADATA);
//        marketProjection.add(MarketProjection.MARKET_START_TIME);

        Integer maxResultsNum = 200;
        
        String maxResults = maxResultsNum.toString();
        
        System.out.println(new Date() + " - Iniciou a adicionar os mercados");
        		
        List<MarketCatalogue> marketCatalogueResult = rescriptOperations.listMarketCatalogue(marketFilter, marketProjection, MarketSort.FIRST_TO_START, maxResults,
                applicationKey, sessionToken);
        System.out.println("marketCatalogueResult qtd: " + marketCatalogueResult.size());
        for (MarketCatalogue marketCatalogue : marketCatalogueResult) {
//			System.out.println(marketCatalogue);
//			printMarketCatalogue(marketCatalogue);
			markets.put(marketCatalogue.getMarketId(), marketCatalogue);
			
			ultimaDataRecebida = marketCatalogue.getEvent().getOpenDate();
			if ( ultimaDataRecebida.after(calFim.getTime()) ) {
				System.out.println("Terminou na data: " + ultimaDataRecebida);
				break;
			}
		}
        
        if ( marketCatalogueResult.size() >= maxResultsNum) {
        	Date ultimaDataRecebidaParaComparar = null;
        	boolean continuarRodando = true;
            while ( ultimaDataRecebida.before(calFim.getTime()) && continuarRodando ) {
            	ultimaDataRecebidaParaComparar = ultimaDataRecebida;
//            	System.out.println("\n\n AINDA SERÃO ADICIONADOS MAIS MERCADOS A PARTIR DA DATA: " + ultimaDataRecebida + ".  ATÉ MOMENTO A QUANTIDADE É DE: " + markets.size());
            	System.out.println("Adicionando mais mercados a partir de  " + ultimaDataRecebida + ".  ATÉ MOMENTO A QUANTIDADE É DE: " + markets.size());
            	time.setFrom(ultimaDataRecebida);
            	marketFilter.setMarketStartTime(time);
            	marketCatalogueResult = rescriptOperations.listMarketCatalogue(marketFilter, marketProjection, MarketSort.FIRST_TO_START, maxResults,
                        applicationKey, sessionToken);
//                System.out.println("marketCatalogueResult qtd: " + marketCatalogueResult.size());
                for (MarketCatalogue marketCatalogue : marketCatalogueResult) {
//    				System.out.println(marketCatalogue);
//    				printMarketCatalogue(marketCatalogue);
    				markets.put(marketCatalogue.getMarketId(), marketCatalogue);
    				ultimaDataRecebida = marketCatalogue.getEvent().getOpenDate();
    				if ( ultimaDataRecebida.after(calFim.getTime()) ) {
    					System.out.println("Terminou na data: " + ultimaDataRecebida);
    					break;
    				}
    			}
                
                if ( ultimaDataRecebidaParaComparar.equals(ultimaDataRecebida)) {
                	continuarRodando = false;
                }
                
            }
            System.out.println(new Date() + " - Terminou de adicionar os mercados");
        }
        
        
        
        Collection<MarketCatalogue> todosOsMercados = markets.values();
        
        for (MarketCatalogue marketCatalogue : todosOsMercados) {
        	eventos.put(marketCatalogue.getEvent().getId(), marketCatalogue.getEvent());
        }
        
        System.out.println("Foram encontrados " + eventos.size() + " eventos agora." );
        
        Map<String, List<MarketCatalogue>> mercadosPorEvento = new LinkedHashMap<String, List<MarketCatalogue>>();
        
        for (MarketCatalogue marketCatalogue : todosOsMercados) {
        	String idEvento = marketCatalogue.getEvent().getId();
        	if ( mercadosPorEvento.get(idEvento) != null) {
        		mercadosPorEvento.get(idEvento).add(marketCatalogue);
        	} else {
        		List<MarketCatalogue> mercadosTemp = new ArrayList<MarketCatalogue>();
        		mercadosTemp.add(marketCatalogue);
        		mercadosPorEvento.put(idEvento, mercadosTemp);
        	}
        }
        
        mercadosPorEventoChaveEValor = mercadosPorEvento.entrySet();
        
    	
    }
    
    
    public void processaMercados(String appKey, String ssoid) {

        this.applicationKey = appKey;
        this.sessionToken = ssoid;

        try {
        	
        	System.out.println("Iniciando em " + new Date());

            final PriceProjection priceProjection = new PriceProjection();
            Set<PriceData> priceData = new HashSet<PriceData>();
            priceData.add(PriceData.EX_ALL_OFFERS);
            priceProjection.setPriceData(priceData);

            final Map<String, Integer> threads = new HashMap<String, Integer>();
            threads.put("qtd", 0);
            
           
            
            mercadosProcessados.clear();
            
            new Thread(new Runnable() {
				
				@Override
				public void run() {
					int cont = 0;
					Date dataInicial = new Date();
					while (true) {
						cont++;
						System.out.println(new Date() + " iniciando eventos do DoubleChance x MatchOdds");
					
						List<Thread> todasAsThreads = new ArrayList<Thread>();
						
						for (final Entry<String, List<MarketCatalogue>> lista : mercadosPorEventoChaveEValor) {
			            	Thread threadTemp = new Thread(new Runnable() {
			            		
								
								@Override
								public void run() {
									try {
										processaEventoMaisRecorrentes(eventos, priceProjection, lista);
										threads.put("qtd", threads.get("qtd") - 1);
									} catch (APINGException e) {
										threads.put("qtd", threads.get("qtd") - 1);
										e.printStackTrace();
									}
									
								}
							});
			            	
			            	while ( threads.get("qtd") > 30 ) {
			            		try {
									Thread.sleep(2000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
			            	}
			            	
			            	threads.put("qtd", threads.get("qtd") + 1);
			        		threadTemp.start();
			        		todasAsThreads.add(threadTemp);
			            	
				        }
						

			            boolean temThreadAtiva = true;
			            while ( temThreadAtiva ) {
			            	int contT = 0;
			            	temThreadAtiva = false;
			            	for (Thread thread : todasAsThreads) {
								if ( thread.isAlive() ) {
									contT++;
									temThreadAtiva = true;
								}
							}
			            	if ( temThreadAtiva ) {
			            		System.out.println("Ainda tem " + contT + " threads ativas. ");
			            		try {
									Thread.sleep(5000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
			            	}
			            }
			            
			            System.out.println("\n\n\n\n RESULTADO FINAL AH - Encontrados: " + encontradosMaiorQueZero.size());
			            System.out.println("piorPorcentagem: " + piorPorcentagem + " e melhorPorcentagem: " + melhorPorcentagem);
			            
			            for (String bet : encontradosMaiorQueZero) {
							System.out.println(bet);
						}
			            
			            System.out.println("FIM DoubleChance x MatchOdds: " + new Date());
					
			            System.out.println(new Date() + " Já rodou DoubleChance x MatchOdds" + cont + " vezes desde " + dataInicial);
			    		System.out.println("- encontradosMaiorQueZeroGeralDoubleChanceXMatchOdds: " + encontradosMaiorQueZeroGeralDoubleChanceXMatchOdds);
			    		adicionaApostaNoCSV("- encontradosMaiorQueZeroGeralDoubleChanceXMatchOdds: " + encontradosMaiorQueZeroGeralDoubleChanceXMatchOdds);
			            
					}
					
				}
			}).start();
            
            
            
            

            new Thread(new Runnable() {
				
				@Override
				public void run() {
					int cont = 0;
					Date dataInicial = new Date();
					while (true) {
						cont++;
            
			            List<Thread> todasAsThreads = new ArrayList<Thread>();
			            
			            for (final Entry<String, List<MarketCatalogue>> lista : mercadosPorEventoChaveEValor) {
			            	Thread threadTemp = new Thread(new Runnable() {
								
								@Override
								public void run() {
									try {
										processaEvento(eventos, priceProjection, lista);
										threads.put("qtd", threads.get("qtd") - 1);
									} catch (APINGException e) {
										threads.put("qtd", threads.get("qtd") - 1);
										e.printStackTrace();
									}
									
								}
							});
			            	
			            	while ( threads.get("qtd") > 30 ) {
			            		try {
									Thread.sleep(2000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
			            	}
			            	
			            	threads.put("qtd", threads.get("qtd") + 1);
			        		threadTemp.start();
			        		todasAsThreads.add(threadTemp);
			            	
				        }
			            
			            boolean temThreadAtiva = true;
			            while ( temThreadAtiva ) {
			            	int contT = 0;
			            	temThreadAtiva = false;
			            	for (Thread thread : todasAsThreads) {
								if ( thread.isAlive() ) {
									contT++;
									temThreadAtiva = true;
								}
							}
			            	if ( temThreadAtiva ) {
			            		System.out.println("Ainda tem " + contT + " threads ativas. ");
			            		try {
									Thread.sleep(5000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
			            	}
			            }
			            
			            System.out.println("\n\n\n\n RESULTADO FINAL - Encontrados: " + encontradosMaiorQueZero.size());
			            System.out.println("piorPorcentagem: " + piorPorcentagem + " e melhorPorcentagem: " + melhorPorcentagem);
			            
			            for (String bet : encontradosMaiorQueZero) {
							System.out.println(bet);
						}
			            
			            System.out.println("FIM: " + new Date());
			            
			            System.out.println(new Date() + " Já rodou outros mercados" + cont + " vezes desde " + dataInicial);
			    		System.out.println("- encontradosMaiorQueZeroGeral: " + encontradosMaiorQueZeroGeral);
			    		adicionaApostaNoCSV("- encontradosMaiorQueZeroGeral: " + encontradosMaiorQueZeroGeral);
            
					}
					
				}
			}).start();

        } catch (Exception apiExc) {
            System.out.println(apiExc.toString());
        }
    }



    private void processaEventoMaisRecorrentes(Map<String, Event> eventos, PriceProjection priceProjection,
    		Entry<String, List<MarketCatalogue>> lista) throws APINGException {
    	OrderProjection orderProjection = null;
    	MatchProjection matchProjection = null;
    	String currencyCode = null;
    	Event eventoEmProcessamento = eventos.get(lista.getKey());
    	System.out.println("Processando evento id: " + eventoEmProcessamento.getId() + " Nome: " + eventoEmProcessamento.getName() + " Data: " + eventoEmProcessamento.getOpenDate());
    	
    	Map<String, List<Runner>> mapaTipos = new HashMap<String, List<Runner>>();
    	
    	piorPorcentagem = 0d;
    	melhorPorcentagem = -5d;
    	encontradosMaiorQueZero = new ArrayList<String>();
    	
    	int contMercadoDentroDoEvento = 0;
    	
    	List<MarketCatalogue> listaDeMercadosDentroDoEvento = new ArrayList<MarketCatalogue>();
    	List<MarketCatalogue> listaDeMercadosDentroDoEventoTmp = lista.getValue();
    	for (MarketCatalogue marketCatalogue : listaDeMercadosDentroDoEventoTmp) {
    		if ( marketCatalogue.getDescription().getMarketType().equals("MATCH_ODDS")  
    				|| marketCatalogue.getDescription().getMarketType().equals("DOUBLE_CHANCE")
    				) {
    			listaDeMercadosDentroDoEvento.add(marketCatalogue);
    		}
    	}
    	
    	
    	for (MarketCatalogue marketCatalogue : listaDeMercadosDentroDoEvento) {
    		contMercadoDentroDoEvento++;
    		try {
    			mercadosProcessados.add(true);
    			System.out.println("\n\n\n MERCADO: " + mercadosProcessados.size() + "    de " + markets.size() + "                            mercado no evento: " + contMercadoDentroDoEvento + " de " + listaDeMercadosDentroDoEvento.size());
    			System.out.println("piorPorcentagem: " + piorPorcentagem + " e melhorPorcentagem: " + melhorPorcentagem);
//				printMarketCatalogue(marketCatalogue);
    			
    			List<String> marketIds = new ArrayList<String>();
    			marketIds.add(marketCatalogue.getMarketId());
    			
    			List<MarketBook> marketBookReturn = null;
    			try {
    				marketBookReturn = rescriptOperations.listMarketBook(marketIds, priceProjection,
    						orderProjection, matchProjection, currencyCode, applicationKey, sessionToken);
    			} catch (Exception e) {
    				Thread.sleep(5000);
    				try {
    					marketBookReturn = rescriptOperations.listMarketBook(marketIds, priceProjection,
    							orderProjection, matchProjection, currencyCode, applicationKey, sessionToken);
    				} catch (Exception e2) {
    					Thread.sleep(10000);
    					marketBookReturn = rescriptOperations.listMarketBook(marketIds, priceProjection,
    							orderProjection, matchProjection, currencyCode, applicationKey, sessionToken);
    				}
    			}
    			
//		        System.out.println("\n\n ----- market books! ----");
    			for (MarketBook marketBook : marketBookReturn) {
    				MarketCatalogue marketAtual = markets.get(marketBook.getMarketId());
    				
    				List<Runner> runners = marketBook.getRunners();
    				for (Runner runner : runners) {
    					runner.setMarketBook(marketBook);
    				}
    				
    				mapaTipos.put(marketAtual.getMarketName(), runners);
    			}
    		} catch (Exception e) {
    			e.printStackTrace();
    			System.out.println("Erro.. passando pro próximo...");
    		}
    	} //terminou de rodar todos os mercados do evento
    	
//    	System.out.println("\n\n\n ---------------------------------------------------------------------");
//    	System.out.println("SUREBETS COMBINADAS entre HANDICAP");
//    	System.out.println(" --------------------------------------------------------------------- \n\n");
    	
    	String nomeEventoAtual = eventoEmProcessamento.getName();
    	if ( nomeEventoAtual.indexOf(" v ") < 0 ) {
    		return;
    	}
    	String nomeTime1 = nomeEventoAtual.split(" v ")[0].trim();
    	String nomeTime2 = nomeEventoAtual.split(" v ")[1].trim();
    	
    	System.out.println("Double chance x Match Odds");
    	System.out.println("\nVERIFICANDO getRunnerPorTipo(mapaTipos, \"Double Chance\", \"Home or Draw\")   x   getRunnerPorTipo(mapaTipos, \"Match Odds\", nomeTime2)");
    	verificaSurebetEAposta(getRunnerPorTipo(mapaTipos, "Double Chance", "Home or Draw"), getRunnerPorTipo(mapaTipos, "Match Odds", nomeTime2));
    	System.out.println("\nVERIFICANDO getRunnerPorTipo(mapaTipos, \"Double Chance\", \"Draw or Away\")   x   getRunnerPorTipo(mapaTipos, \"Match Odds\", nomeTime1)");
    	verificaSurebetEAposta(getRunnerPorTipo(mapaTipos, "Double Chance", "Draw or Away"), getRunnerPorTipo(mapaTipos, "Match Odds", nomeTime1));
    	System.out.println("\nVERIFICANDO getRunnerPorTipo(mapaTipos, \"Double Chance\", \"Home or Away\")   x   getRunnerPorTipo(mapaTipos, \"Match Odds\", \"The Draw\")");
    	verificaSurebetEAposta(getRunnerPorTipo(mapaTipos, "Double Chance", "Home or Away"), getRunnerPorTipo(mapaTipos, "Match Odds", "The Draw"));
    	
    	System.out.println("\n\n\n\n RESULTADO do EVENTO mais recorrentes id: " + eventoEmProcessamento.getId() + " Nome: " + eventoEmProcessamento.getName() + " Data: " + eventoEmProcessamento.getOpenDate());
    	System.out.println("- Encontrados: " + encontradosMaiorQueZero.size());
    	System.out.println("- piorPorcentagem: " + piorPorcentagem + " e melhorPorcentagem: " + melhorPorcentagem);
    	
    	if ( piorPorcentagem < piorPorcentagemGeral ) {
    		piorPorcentagemGeral = piorPorcentagem.doubleValue();
    	}
    	
    	if ( melhorPorcentagem > melhorPorcentagemGeral ) {
    		melhorPorcentagemGeral = melhorPorcentagem.doubleValue();
    	}
    	
    	//FIM, vai pro próximo EVENTO
    }
    
    
	private void processaEvento(Map<String, Event> eventos, PriceProjection priceProjection,
			Entry<String, List<MarketCatalogue>> lista) throws APINGException {
		OrderProjection orderProjection = null;
        MatchProjection matchProjection = null;
        String currencyCode = null;
		Event eventoEmProcessamento = eventos.get(lista.getKey());
		System.out.println("Processando evento id: " + eventoEmProcessamento.getId() + " Nome: " + eventoEmProcessamento.getName() + " Data: " + eventoEmProcessamento.getOpenDate());
		
		Map<String, List<Runner>> mapaTipos = new HashMap<String, List<Runner>>();
		
		piorPorcentagem = 0d;
		melhorPorcentagem = -5d;
		encontradosMaiorQueZero = new ArrayList<String>();
		
		int contMercadoDentroDoEvento = 0;

    	List<MarketCatalogue> listaDeMercadosDentroDoEvento = new ArrayList<MarketCatalogue>();
    	List<MarketCatalogue> listaDeMercadosDentroDoEventoTmp = lista.getValue();
    	for (MarketCatalogue marketCatalogue : listaDeMercadosDentroDoEventoTmp) {
    		if ( !marketCatalogue.getDescription().getMarketType().equals("MATCH_ODDS")  
    				&& !marketCatalogue.getDescription().getMarketType().equals("DOUBLE_CHANCE")
    				) {
    			listaDeMercadosDentroDoEvento.add(marketCatalogue);
    		}
    	}
		
		
		for (MarketCatalogue marketCatalogue : listaDeMercadosDentroDoEvento) {
			contMercadoDentroDoEvento++;
			try {
				mercadosProcessados.add(true);
		    	System.out.println("\n\n\n MERCADO: " + mercadosProcessados.size() + "    de " + markets.size() + "                            mercado no evento: " + contMercadoDentroDoEvento + " de " + listaDeMercadosDentroDoEvento.size());
		    	System.out.println("piorPorcentagem: " + piorPorcentagem + " e melhorPorcentagem: " + melhorPorcentagem);
//				printMarketCatalogue(marketCatalogue);

		        List<String> marketIds = new ArrayList<String>();
				marketIds.add(marketCatalogue.getMarketId());
				
				List<MarketBook> marketBookReturn = null;
				try {
					marketBookReturn = rescriptOperations.listMarketBook(marketIds, priceProjection,
		                orderProjection, matchProjection, currencyCode, applicationKey, sessionToken);
				} catch (Exception e) {
					Thread.sleep(5000);
					try {
						marketBookReturn = rescriptOperations.listMarketBook(marketIds, priceProjection,
			                orderProjection, matchProjection, currencyCode, applicationKey, sessionToken);
					} catch (Exception e2) {
						Thread.sleep(10000);
						marketBookReturn = rescriptOperations.listMarketBook(marketIds, priceProjection,
				                orderProjection, matchProjection, currencyCode, applicationKey, sessionToken);
					}
				}

//		        System.out.println("\n\n ----- market books! ----");
		        for (MarketBook marketBook : marketBookReturn) {
		        	MarketCatalogue marketAtual = markets.get(marketBook.getMarketId());
					
					List<Runner> runners = marketBook.getRunners();
					for (Runner runner : runners) {
						runner.setMarketBook(marketBook);
					}
					
					List<RunnerCatalog> runnerCatalogs = marketCatalogue.getRunners();
					
					
					mapaTipos.put(marketAtual.getMarketName(), runners);
					
					
					if ( runners.size() == 2 ) {
//						System.out.println("\n\n ----- market: ----");
//						System.out.println(marketBook);
//						System.out.println(marketAtual);
//						printMarketCatalogue(marketAtual);
						for (Runner runner : runners) {
//							System.out.println(runner);
							if ( runner.getHandicap() != null ) {
//								System.out.println("Handicap: " + runner.getHandicap() + " Preço: " + runner.getLastPriceTraded());
								RunnerCatalog runnerCatalogAtual = getRunnerCatalog(runnerCatalogs, runner);
//								System.out.println("Exibindo preços para a opção id: " + runnerCatalogAtual.getSelectionId() + " nome: " + runnerCatalogAtual.getRunnerName()); 
								ExchangePrices ex = runner.getEx();
								for (PriceSize priceSize : ex.getAvailableToBack()) {
//									System.out.println("Preço: " + priceSize.getPrice() + " Size: " + priceSize.getSize());
								}
//								List<PriceSize> avLay = ex.getAvailableToLay();
								
							}
						}
						
						Double odd1TopoDaLista = null;
						Double odd2TopoDaLista = null;
						
						Double odd1Size = null;
						Double odd2Size = null;
						
						Runner runner1 = runners.get(0);
						Runner runner2 = runners.get(1);
						Double porcentagem = 0d;
							
						if ( runner1.getEx() != null 
								&& runner1.getEx().getAvailableToBack() != null
								&& runner1.getEx().getAvailableToBack().size() > 0) {
							odd1TopoDaLista = runner1.getEx().getAvailableToBack().get(0).getPrice();
							odd1Size = runner1.getEx().getAvailableToBack().get(0).getSize();
						}
						if ( runner2.getEx() != null 
								&& runner2.getEx().getAvailableToBack() != null
								&& runner2.getEx().getAvailableToBack().size() > 0) {
							odd2TopoDaLista = runner2.getEx().getAvailableToBack().get(0).getPrice();
							odd2Size = runner2.getEx().getAvailableToBack().get(0).getSize();
						}
						
						if ( odd1TopoDaLista == null || odd2TopoDaLista == null ) {
							System.out.println("Faltou oferta");
							continue;
						}
						
						System.out.println("Odds - odd1TopoDaLista: " + odd1TopoDaLista +
								" odd2TopoDaLista: " + odd2TopoDaLista);
						
						Double stake = 50d; 
						Double odd1 = odd1TopoDaLista;
						Double odd2 = odd2TopoDaLista;
						Double stake1 = round(stake-((odd1*stake)/(odd1+odd2)), 2);
						Double stake2 = round(stake-((odd2*stake)/(odd1+odd2)), 2);
						
						System.out.println(" stake1: " + stake1 + " odd1Size: " + odd1Size 
								+ " stake2: " + stake2 + " odd2Size: " + odd2Size);
						
						if ( stake1 > odd1Size || stake2 > odd2Size ) {
							System.out.println(" Não tem saldo diponsível para apostarmos mais. ");
							continue;
						}
						
						Double porcentagem1 = (stake1*odd1)-stake;
						Double porcentagem2 = (stake2*odd2)-stake;
						System.out.println("Porcentagem 1: " + porcentagem1 + " Porcentagem2: " + porcentagem2 +
								" stake1: " + stake1 + " stake2: " + stake2);
						porcentagem = porcentagem1 < porcentagem2 ? porcentagem1 : porcentagem2;
						System.out.println("SUREBET Porcentagem: " + decimalFormat.format(porcentagem));
						
						if ( porcentagem < piorPorcentagem ) {
							piorPorcentagem = porcentagem;
						}
						if ( porcentagem > melhorPorcentagem ) {
							melhorPorcentagem = porcentagem;
						}
						
						if ( porcentagem > 0 ) {
							
							System.out.println("FAZENDO BETTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT");
							System.out.println("Stake1: " + stake1);
							System.out.println("Stake2: " + stake2);
							
							


							MarketBook marketBookR1 = runner1.getMarketBook();
							MarketCatalogue marketCatalogueR1 = markets.get(marketBookR1.getMarketId());
							MarketBook marketBookR2 = runner2.getMarketBook();
							MarketCatalogue marketCatalogueR2 = markets.get(marketBookR2.getMarketId());
							
							String runner1Opcao = "";
							for (RunnerCatalog runnerCatalog : marketCatalogueR1.getRunners()) {
								if ( runner1.getSelectionId().longValue() == runnerCatalog.getSelectionId().longValue() ) {
									runner1Opcao = runnerCatalog.getRunnerName();
									System.out.println("runner 1: " + runner1Opcao);
									break;
								}
							}
							
							String runner2Opcao = "";
							for (RunnerCatalog runnerCatalog : marketCatalogueR2.getRunners()) {
								if ( runner2.getSelectionId().longValue() == runnerCatalog.getSelectionId().longValue() ) {
									runner2Opcao = runnerCatalog.getRunnerName();
									System.out.println("runner 2: " + runner2Opcao);
									break;
								}
							}
							
							
							String betEncontrada = "\n\nMarketId R1 = " + marketCatalogueR1.getMarketId() + " market R1 = " + marketCatalogueR1.getMarketName() + " (opção: " + runner1Opcao + ")"
											+ "      x     " + "MarketId R2 = " + marketCatalogueR2.getMarketId() + " market R2 = " + marketCatalogueR2.getMarketName() + " (opção: " + runner2Opcao + ")" 
											+ "\n Event: " + marketCatalogueR1.getEvent() 
											+ "\nPorcentagem 1: " + porcentagem1 + " Porcentagem2: " + porcentagem2 +
											" stake1: " + stake1 + " stake2: " + stake2  + " odd1Size: " + odd1Size + " odd2Size: " + odd2Size;
							
							System.out.println("Preços R1: " + marketCatalogueR1.getMarketName() + " (opção: " + runner1Opcao + ")");
							for (PriceSize priceSize : runner1.getEx().getAvailableToBack()) {
								System.out.println("Preço: " + priceSize.getPrice() + " Size: " + priceSize.getSize());
							}
							
							System.out.println("Preços R2: " + marketCatalogueR2.getMarketName() + " (opção: " + runner2Opcao + ")");
							for (PriceSize priceSize : runner2.getEx().getAvailableToBack()) {
								System.out.println("Preço: " + priceSize.getPrice() + " Size: " + priceSize.getSize());
							}
							
							adicionaApostaNoCSV(betEncontrada);
							
							encontradosMaiorQueZero.add(betEncontrada);
							

//							String colunas = "dataEncontrado;marketId;evento;pais;dataEvento;market1;opcao1;market2;opcao2;odds1;odds2;stake1;stakeDisponivel1;stake2;stakeDisponivel2;lucro;porcentagem_stake_50";
							
							double lucroEmReais = round(porcentagem, 2);
							String linhaAposta = getDataEHoraAgora() 
									+ ";" + marketCatalogueR1.getMarketId() 
									+ ";" + marketCatalogueR1.getEvent().getName() 
									+ ";" + marketCatalogueR1.getEvent().getCountryCode()
									+ ";" + getDataFormatada(marketCatalogueR1.getEvent().getOpenDate())
									+ ";" + marketCatalogueR1.getMarketName() 
									+ ";" + runner1Opcao
									+ ";" + marketCatalogueR2.getMarketName() 
									+ ";" + runner2Opcao
									+ ";" + odd1
									+ ";" + odd2
									+ ";" + stake1
									+ ";" + odd1Size
									+ ";" + stake2
									+ ";" + odd2Size
									+ ";" + lucroEmReais
									+ ";" + (lucroEmReais * 100 / 50)
									;
							adicionaApostaNoCSVParaAuditoria(linhaAposta);
							
								
								if ( stake1 < stake2) {
						            boolean apostadoComSucesso1 = apostar(marketBook, runner1, stake1, customerRef.toString());
						            if ( apostadoComSucesso1 ) {
							            customerRef++;
							            boolean apostadoComSucesso2 = apostar(marketBook, runner2, stake2, customerRef.toString());	
						            }
								} else {
									boolean apostadoComSucesso2 = apostar(marketBook, runner2, stake2, customerRef.toString());
									if ( apostadoComSucesso2 ) {
										customerRef++;
										boolean apostadoComSucesso1 = apostar(marketBook, runner1, stake1, customerRef.toString());
									}
									
								}
			                
							
							
							
							
						}
					} else {
						
						
						for (Runner runner : runners) {
							System.out.println(runner);
							if ( runner.getHandicap() != null ) {
								System.out.println("Handicap: " + runner.getHandicap() + " Preço: " + runner.getLastPriceTraded());
								RunnerCatalog runnerCatalogAtual = getRunnerCatalog(runnerCatalogs, runner);
								System.out.println("Exibindo preços para a opção id: " + runnerCatalogAtual.getSelectionId() + " nome: " + runnerCatalogAtual.getRunnerName());
								ExchangePrices ex = runner.getEx();
								for (PriceSize priceSize : ex.getAvailableToBack()) {
									System.out.println("Preço: " + priceSize.getPrice() + " Size: " + priceSize.getSize());
								}
//								List<PriceSize> avLay = ex.getAvailableToLay();
								
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Erro.. passando pro próximo...");
			}
		} //terminou de rodar todos os mercados do evento
		
		//TODO: Comparar possíveis Mercados
		
		System.out.println("\n\n\n ---------------------------------------------------------------------");
		System.out.println("SUREBETS COMBINADAS");
		System.out.println(" --------------------------------------------------------------------- \n\n");
		
		String nomeEventoAtual = eventoEmProcessamento.getName();
		if ( nomeEventoAtual.indexOf(" v ") < 0 ) {
			return;
		}
		String nomeTime1 = nomeEventoAtual.split(" v ")[0].trim();
		String nomeTime2 = nomeEventoAtual.split(" v ")[1].trim();
		
//				getTimeCleanSheet(mapaTipos, nomeTime1, "yes");
//				getTimeCleanSheet(mapaTipos, nomeTime1, "no");
//				getTimeCleanSheet(mapaTipos, nomeTime2, "yes");
//				getTimeCleanSheet(mapaTipos, nomeTime2, "no");
//				
//				getOverUnderGoals(mapaTipos, "1.5", "over");
//				getOverUnderGoals(mapaTipos, "1.5", "under");
//				
//				getTimeWinToNil(mapaTipos, nomeTime1, "yes");
//				getTimeWinToNil(mapaTipos, nomeTime1, "no");
//				getTimeWinToNil(mapaTipos, nomeTime2, "yes");
//				getTimeWinToNil(mapaTipos, nomeTime2, "no");
//				
//				getMatchOddsAndBTTS(mapaTipos, "time1-yes");
//				getMatchOddsAndBTTS(mapaTipos, "time2-yes");
//				getMatchOddsAndBTTS(mapaTipos, "draw-yes");
//				getMatchOddsAndBTTS(mapaTipos, "time1-no");
//				getMatchOddsAndBTTS(mapaTipos, "time2-no");
//				getMatchOddsAndBTTS(mapaTipos, "draw-no");
//				
//				getNextGoal(mapaTipos, "time1");
//				getNextGoal(mapaTipos, "time2");
//				getNextGoal(mapaTipos, "no-goal");
//				
//				getBtts(mapaTipos, "yes");
//				getBtts(mapaTipos, "no");
//				
//				getOddOrEven(mapaTipos, "odd");
//				getOddOrEven(mapaTipos, "even");
//				
//				getCorrectScore(mapaTipos, "0 - 0");
//				getCorrectScore(mapaTipos, "0 - 1");
		
//		getRunnerPorTipo(mapaTipos, "Double Chance", "Home or Draw");
//		getRunnerPorTipo(mapaTipos, "Double Chance", "Draw or Away");
//		getRunnerPorTipo(mapaTipos, "Double Chance", "Home or Away");
//		
//		getRunnerPorTipo(mapaTipos, "Match Odds", "nomeTime1");
//		getRunnerPorTipo(mapaTipos, "Match Odds", "nomeTime2");
//		getRunnerPorTipo(mapaTipos, "Match Odds", "The Draw");
		
//		System.out.println("Double chance x Match Odds");
//		System.out.println("\nVERIFICANDO getRunnerPorTipo(mapaTipos, \"Double Chance\", \"Home or Draw\")   x   getRunnerPorTipo(mapaTipos, \"Match Odds\", nomeTime2)");
//		verificaSurebetEAposta(getRunnerPorTipo(mapaTipos, "Double Chance", "Home or Draw"), getRunnerPorTipo(mapaTipos, "Match Odds", nomeTime2));
//		System.out.println("\nVERIFICANDO getRunnerPorTipo(mapaTipos, \"Double Chance\", \"Draw or Away\")   x   getRunnerPorTipo(mapaTipos, \"Match Odds\", nomeTime1)");
//		verificaSurebetEAposta(getRunnerPorTipo(mapaTipos, "Double Chance", "Draw or Away"), getRunnerPorTipo(mapaTipos, "Match Odds", nomeTime1));
//		System.out.println("\nVERIFICANDO getRunnerPorTipo(mapaTipos, \"Double Chance\", \"Home or Away\")   x   getRunnerPorTipo(mapaTipos, \"Match Odds\", \"The Draw\")");
//		verificaSurebetEAposta(getRunnerPorTipo(mapaTipos, "Double Chance", "Home or Away"), getRunnerPorTipo(mapaTipos, "Match Odds", "The Draw"));
		
		System.out.println("\nVERIFICANDO getCorrectScore(mapaTipos, \"0 - 0\")   x   getOverUnderGoals(mapaTipos, \"0.5\", \"over\")");
		verificaSurebetEAposta(getCorrectScore(mapaTipos, "0 - 0"), getOverUnderGoals(mapaTipos, "0.5", "over"));
		
		System.out.println("\nVERIFICANDO getRunnerPorTipo(mapaTipos, \"Time Of First Goal\", \"No Goal\")   x   getOverUnderGoals(mapaTipos, \"0.5\", \"over\")");
		verificaSurebetEAposta(getRunnerPorTipo(mapaTipos, "Time Of First Goal", "No Goal"), getOverUnderGoals(mapaTipos, "0.5", "over"));
		
		
		System.out.println("\nVERIFICANDO getMatchOddsAndBTTS(mapaTipos, \"draw-no\")   x   getOverUnderGoals(mapaTipos, \"0.5\", \"over\")");
		verificaSurebetEAposta(getMatchOddsAndBTTS(mapaTipos, "draw-no"), getOverUnderGoals(mapaTipos, "0.5", "over"));
		
		System.out.println("\nVERIFICANDO getBtts(mapaTipos, \"no\")   x   getOverUnderGoals(mapaTipos, \"0.5\", \"over\")");
		verificaSurebetEAposta(getBtts(mapaTipos, "no"), getOverUnderGoals(mapaTipos, "0.5", "over"));
		
		System.out.println("\nVERIFICANDO getNextGoal(mapaTipos, \"no-goal\")   x   getOverUnderGoals(mapaTipos, \"0.5\", \"over\")");
		verificaSurebetEAposta(getNextGoal(mapaTipos, "no-goal"), getOverUnderGoals(mapaTipos, "0.5", "over"));
		
		
		/*Exemplo no jogo Inter x Napoli:
		 *  Mercado: Inter +1
		 *  Inter +1
		 *  Napoli -1
		 *  Draw
		 */
//				getEuropeanHandicap(mapaTipos, nomeTime1 + " +1", nomeTime2 + " -1");
//				getAsianHandicap(mapaTipos, nomeTime1, 1.5);
//				
//				
//				getEuropeanHandicap(mapaTipos, nomeTime1 + " +1", nomeTime2 + " +1");
//				getAsianHandicap(mapaTipos, nomeTime1, -0.5);
		
		
		
		//time A negativo
		System.out.println("\nVERIFICANDO getAsianHandicap(mapaTipos, nomeTime1, -0.5)   x   getEuropeanHandicap(mapaTipos, nomeTime2 + \" +1\", nomeTime2 + \" +1\")");
		verificaSurebetEAposta(getAsianHandicap(mapaTipos, nomeTime1, -0.5), getEuropeanHandicap(mapaTipos, nomeTime2 + " +1", nomeTime2 + " +1"));
		System.out.println("\nVERIFICANDO getAsianHandicap(mapaTipos, nomeTime1, -1.5)   x   getEuropeanHandicap(mapaTipos, nomeTime2 + \" +2\", nomeTime2 + \" +2\")");
		verificaSurebetEAposta(getAsianHandicap(mapaTipos, nomeTime1, -1.5), getEuropeanHandicap(mapaTipos, nomeTime2 + " +2", nomeTime2 + " +2"));
		System.out.println("\nVERIFICANDO getAsianHandicap(mapaTipos, nomeTime1, -2.5)   x   getEuropeanHandicap(mapaTipos, nomeTime2 + \" +3\", nomeTime2 + \" +3\")");
		verificaSurebetEAposta(getAsianHandicap(mapaTipos, nomeTime1, -2.5), getEuropeanHandicap(mapaTipos, nomeTime2 + " +3", nomeTime2 + " +3"));
		
		//time A positivo
		System.out.println("\nVERIFICANDO getAsianHandicap(mapaTipos, nomeTime1, -0.5)   x   getEuropeanHandicap(mapaTipos, nomeTime1 + \" +1\", nomeTime2 + \" -1\")");
		verificaSurebetEAposta(getAsianHandicap(mapaTipos, nomeTime1, 1.5), getEuropeanHandicap(mapaTipos, nomeTime1 + " +1", nomeTime2 + " -1"));
		System.out.println("\nVERIFICANDO getAsianHandicap(mapaTipos, nomeTime1, -1.5)   x   getEuropeanHandicap(mapaTipos, nomeTime1 + \" +1\", nomeTime2 + \" -2\")");
		verificaSurebetEAposta(getAsianHandicap(mapaTipos, nomeTime1, 2.5), getEuropeanHandicap(mapaTipos, nomeTime1 + " +1", nomeTime2 + " -2"));
		System.out.println("\nVERIFICANDO getAsianHandicap(mapaTipos, nomeTime1, -2.5)   x   getEuropeanHandicap(mapaTipos, nomeTime1 + \" +1\", nomeTime2 + \" -3\")");
		verificaSurebetEAposta(getAsianHandicap(mapaTipos, nomeTime1, 3.5), getEuropeanHandicap(mapaTipos, nomeTime1 + " +1", nomeTime2 + " -3"));
		
		List<Runner> runnersAH = mapaTipos.get("Asian Handicap");
		//\nVERIFICANDO um handicap contra o outro
		if ( runnersAH != null ) {
			for ( int contAH = 0; contAH < runnersAH.size(); contAH++ ) {
				System.out.println("\nVERIFICANDO Surebet AsianHandicap " + (contAH + 1) + " de " + runnersAH.size());
				Runner runner1 = runnersAH.get(contAH);
				contAH++;
				Runner runner2 = runnersAH.get(contAH);
				System.out.println(runner1);
				System.out.println(runner2);
				System.out.println("------------:");
				verificaSurebetEAposta(runner1, runner2);
			}
		}
		
		System.out.println("\n\n\n\n RESULTADO do EVENTO id: " + eventoEmProcessamento.getId() + " Nome: " + eventoEmProcessamento.getName() + " Data: " + eventoEmProcessamento.getOpenDate());
		System.out.println("- Encontrados: " + encontradosMaiorQueZero.size());
		System.out.println("- piorPorcentagem: " + piorPorcentagem + " e melhorPorcentagem: " + melhorPorcentagem);
		
		if ( piorPorcentagem < piorPorcentagemGeral ) {
			piorPorcentagemGeral = piorPorcentagem.doubleValue();
		}
		
		if ( melhorPorcentagem > melhorPorcentagemGeral ) {
			melhorPorcentagemGeral = melhorPorcentagem.doubleValue();
		}
		
		//FIM, vai pro próximo EVENTO
	}


	
	private Runner getTimeCleanSheet(Map<String, List<Runner>> mapaTipos, String nomeTime, String tipo) {
		List<Runner> runners = mapaTipos.get(nomeTime + " Clean Sheet");
		if ( runners == null || runners.size() == 0) {
			return null;
		}
		Runner timeCleanSheetRunnerYes = runners.get(0);
		Runner time2CleanSheetRunnerNo = runners.get(1);
		
		if ( tipo.equals("yes")) {
			return timeCleanSheetRunnerYes;
		} else if ( tipo.equals("no")) {
			return time2CleanSheetRunnerNo;
		} else {
			return null;
		}
	}
	

	private Runner getOverUnderGoals(Map<String, List<Runner>> mapaTipos, String qtd, String tipo) {
		List<Runner> runners = mapaTipos.get("Over/Under " + qtd + " Goals");
		if ( runners == null || runners.size() == 0) {
			return null;
		}
		Runner overUnderGoalsRunnerUnder = runners.get(0);
		Runner overUnderGoalsRunnerOver = runners.get(1);
		if ( tipo.equals("over") ) {
			return overUnderGoalsRunnerOver;
		} else if ( tipo.equals("under") ) {
			return overUnderGoalsRunnerUnder;
		} else {
			return null;
		}
	}



	private Runner getTimeWinToNil(Map<String, List<Runner>> mapaTipos, String nomeTime, String tipo) {
		List<Runner> runners = mapaTipos.get(nomeTime + " Win to Nil");
		if ( runners == null || runners.size() == 0) {
			return null;
		}
		Runner timeWinToNilRunnerYes = runners.get(0);
		Runner timeWinToNilRunnerNo = runners.get(1);
		if ( tipo.equals("yes")) {
			return timeWinToNilRunnerYes;
		} else if ( tipo.equals("no")) {
			return timeWinToNilRunnerNo;
		} else {
			return null;
		}
	}
	
	private Runner getMatchOddsAndBTTS(Map<String, List<Runner>> mapaTipos, String tipo) {
		List<Runner> runners = mapaTipos.get("Match Odds and Both teams to Score");
		if ( runners == null || runners.size() == 0) {
			return null;
		}
		Runner matchOddsAndBTTSRunnerTime1Yes = runners.get(0);
		Runner matchOddsAndBTTSRunnerTime2Yes = runners.get(1);
		Runner matchOddsAndBTTSRunnerDrawYes = runners.get(2);
		Runner matchOddsAndBTTSRunnerTime1No = runners.get(3);
		Runner matchOddsAndBTTSRunnerTime2No = runners.get(4);
		Runner matchOddsAndBTTSRunnerDrawNo = runners.get(5);
		
		if ( tipo.equals("time1-yes") ) {
			return matchOddsAndBTTSRunnerTime1Yes;
		} else if ( tipo.equals("time2-yes") ) {
			return matchOddsAndBTTSRunnerTime2Yes;
		} else if ( tipo.equals("draw-yes") ) {
			return matchOddsAndBTTSRunnerDrawYes;
		} else if ( tipo.equals("time1-no") ) {
			return matchOddsAndBTTSRunnerTime1No;
		} else if ( tipo.equals("time2-no") ) {
			return matchOddsAndBTTSRunnerTime2No;
		} else if ( tipo.equals("draw-no") ) {
			return matchOddsAndBTTSRunnerDrawNo;
		} else {
			return null;
		}
	}
	

	private Runner getBtts(Map<String, List<Runner>> mapaTipos, String tipo) {
		List<Runner> runners = mapaTipos.get("Both teams to Score?");
		if ( runners == null || runners.size() == 0) {
			return null;
		}
		Runner bttsRunnerYes = runners.get(0);
		Runner bttsRunnerNo = runners.get(1);
		if ( tipo.equals("yes")) {
			return bttsRunnerYes;
		} else if ( tipo.equals("no")) {
			return bttsRunnerNo;
		} else {
			return null;
		}
	}
	
	private Runner getNextGoal(Map<String, List<Runner>> mapaTipos, String tipo) {
		List<Runner> runners = mapaTipos.get("Next Goal");
		if ( runners == null || runners.size() == 0) {
			return null;
		}
		Runner bttsRunnerTime1 = runners.get(0);
		Runner bttsRunnerTime2 = runners.get(1);
		Runner bttsRunnerNoGoal = runners.get(2);
		if ( tipo.equals("time1")) {
			return bttsRunnerTime1;
		} else if ( tipo.equals("time2")) {
			return bttsRunnerTime2;
		} else if ( tipo.equals("no-goal")) {
			return bttsRunnerNoGoal;
		} else {
			return null;
		}
	}

	
	private Runner getOddOrEven(Map<String, List<Runner>> mapaTipos, String tipo) {
		List<Runner> runners = mapaTipos.get("Odd or Even");
		if ( runners == null || runners.size() == 0) {
			return null;
		}
		Runner bttsRunnerOdd = runners.get(0);
		Runner bttsRunnerEven = runners.get(1);
		if ( tipo.equals("odd")) {
			return bttsRunnerOdd;
		} else if ( tipo.equals("even")) {
			return bttsRunnerEven;
		} else {
			return null;
		}
	}
	
	private Runner getCorrectScore(Map<String, List<Runner>> mapaTipos, String placar) {
		List<Runner> runners = mapaTipos.get("Correct Score");
		if ( runners == null || runners.size() == 0) {
			return null;
		}
		MarketBook marketBook = runners.get(0).getMarketBook();
		MarketCatalogue marketCatalogue = markets.get(marketBook.getMarketId());
		
		List<RunnerCatalog> runnersCatalog = marketCatalogue.getRunners();
		Long selectedId = null;
		for (RunnerCatalog runnerCatalog : runnersCatalog) {
			if ( placar.equals(runnerCatalog.getRunnerName())) {
				selectedId = runnerCatalog.getSelectionId();
				break;
			}
		}
		if (selectedId == null) {
			return null;
		}
		
		for (Runner runner : runners) {
			if ( runner.getSelectionId().compareTo(selectedId) == 0) {
				return runner;
			}
		}
		
		return null;
		
	}
	
	private Runner getRunnerPorTipo(Map<String, List<Runner>> mapaTipos, String nomeMercado, String nomeRunner) {
		List<Runner> runners = mapaTipos.get(nomeMercado);
		if ( runners == null || runners.size() == 0) {
			return null;
		}
		MarketBook marketBook = runners.get(0).getMarketBook();
		MarketCatalogue marketCatalogue = markets.get(marketBook.getMarketId());
		
		List<RunnerCatalog> runnersCatalog = marketCatalogue.getRunners();
		Long selectedId = null;
		for (RunnerCatalog runnerCatalog : runnersCatalog) {
			if ( nomeRunner.equals(runnerCatalog.getRunnerName())) {
				selectedId = runnerCatalog.getSelectionId();
				break;
			}
		}
		if (selectedId == null) {
			return null;
		}
		
		for (Runner runner : runners) {
			if ( runner.getSelectionId().compareTo(selectedId) == 0) {
				return runner;
			}
		}
		
		return null;
		
	}
	
	private Runner getEuropeanHandicap(Map<String, List<Runner>> mapaTipos, String nomeMercado, String tipo) {
		List<Runner> runners = mapaTipos.get(nomeMercado);
		if ( runners == null || runners.size() == 0) {
			return null;
		}
		MarketBook marketBook = runners.get(0).getMarketBook();
		MarketCatalogue marketCatalogue = markets.get(marketBook.getMarketId());
		
		List<RunnerCatalog> runnersCatalog = marketCatalogue.getRunners();
		Long selectedId = null;
		for (RunnerCatalog runnerCatalog : runnersCatalog) {
			if ( tipo.equals(runnerCatalog.getRunnerName())) {
				selectedId = runnerCatalog.getSelectionId();
				break;
			}
		}
		if (selectedId == null) {
			return null;
		}
		
		for (Runner runner : runners) {
			if ( runner.getSelectionId().compareTo(selectedId) == 0) {
				return runner;
			}
		}
		
		return null;
		
	}
	
	
	private Runner getAsianHandicap(Map<String, List<Runner>> mapaTipos, String nomeTime, double handicap) {
		List<Runner> runners = mapaTipos.get("Asian Handicap");
		if ( runners == null || runners.size() == 0) {
			return null;
		}
		MarketBook marketBook = runners.get(0).getMarketBook();
		MarketCatalogue marketCatalogue = markets.get(marketBook.getMarketId());
		
		List<RunnerCatalog> runnersCatalog = marketCatalogue.getRunners();
		Long selectedId = null;
		for (RunnerCatalog runnerCatalog : runnersCatalog) {
			if ( nomeTime.equals(runnerCatalog.getRunnerName())) {
				selectedId = runnerCatalog.getSelectionId();
				break;
			}
		}
		if (selectedId == null) {
			return null;
		}
		
		for (Runner runner : runners) {
			if ( runner.getSelectionId().compareTo(selectedId) == 0 
					&& runner.getHandicap().doubleValue() == handicap) {
				return runner;
			}
		}
		
		return null;
		
	}

	

    
    private void verificaSurebetEAposta(Runner runner1, Runner runner2) {
    	Double odd1TopoDaLista = null;
		Double odd2TopoDaLista = null;
		
		Double odd1Size = null;
		Double odd2Size = null;
		
		Double porcentagem = 0d;
		
		if ( runner1 != null 
				&& runner1.getEx() != null 
				&& runner1.getEx().getAvailableToBack() != null
				&& runner1.getEx().getAvailableToBack().size() > 0) {
			odd1TopoDaLista = runner1.getEx().getAvailableToBack().get(0).getPrice();
			odd1Size = runner1.getEx().getAvailableToBack().get(0).getSize();
		}
		if ( runner2 != null
				&& runner2.getEx() != null 
				&& runner2.getEx().getAvailableToBack() != null
				&& runner2.getEx().getAvailableToBack().size() > 0) {
			odd2TopoDaLista = runner2.getEx().getAvailableToBack().get(0).getPrice();
			odd2Size = runner2.getEx().getAvailableToBack().get(0).getSize();
		}
		
		if ( odd1TopoDaLista == null || odd2TopoDaLista == null ) {
			System.out.println("Faltou oferta");
			return;
		}
		
		System.out.println("Odds - odd1TopoDaLista: " + odd1TopoDaLista +
				" odd2TopoDaLista: " + odd2TopoDaLista);
		
		Double stake = 50d; 
		Double odd1 = odd1TopoDaLista;
		Double odd2 = odd2TopoDaLista;
		Double stake1 = round(stake-((odd1*stake)/(odd1+odd2)), 2);
		Double stake2 = round(stake-((odd2*stake)/(odd1+odd2)), 2);
		
		System.out.println(" stake1: " + stake1 + " odd1Size: " + odd1Size 
				+ " stake2: " + stake2 + " odd2Size: " + odd2Size);
		
		if ( stake1 > odd1Size || stake2 > odd2Size ) {
			System.out.println(" Não tem saldo diponsível para apostarmos mais. ");
			return;
		}
		
		Double porcentagem1 = (stake1*odd1)-stake;
		Double porcentagem2 = (stake2*odd2)-stake;
		System.out.println("Porcentagem 1: " + porcentagem1 + " Porcentagem2: " + porcentagem2 +
				" stake1: " + stake1 + " stake2: " + stake2);
		porcentagem = porcentagem1 < porcentagem2 ? porcentagem1 : porcentagem2;
		System.out.println("SUREBET Porcentagem: " + decimalFormat.format(porcentagem));
		
		if ( porcentagem < piorPorcentagem ) {
			piorPorcentagem = porcentagem;
		}
		if ( porcentagem > melhorPorcentagem ) {
			melhorPorcentagem = porcentagem;
		}
		
		if ( porcentagem > 0 ) {
			
			System.out.println("FAZENDO BETTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT");
			System.out.println("Stake1: " + stake1);
			System.out.println("Stake2: " + stake2);
			
			

			MarketBook marketBookR1 = runner1.getMarketBook();
			MarketCatalogue marketCatalogueR1 = markets.get(marketBookR1.getMarketId());
			MarketBook marketBookR2 = runner2.getMarketBook();
			MarketCatalogue marketCatalogueR2 = markets.get(marketBookR2.getMarketId());
			
			String runner1Opcao = "";
			for (RunnerCatalog runnerCatalog : marketCatalogueR1.getRunners()) {
				if ( runner1.getSelectionId().longValue() == runnerCatalog.getSelectionId().longValue() ) {
					runner1Opcao = runnerCatalog.getRunnerName();
					System.out.println("runner 1: " + runner1Opcao);
					break;
				}
			}
			
			String runner2Opcao = "";
			for (RunnerCatalog runnerCatalog : marketCatalogueR2.getRunners()) {
				if ( runner2.getSelectionId().longValue() == runnerCatalog.getSelectionId().longValue() ) {
					runner2Opcao = runnerCatalog.getRunnerName();
					System.out.println("runner 2: " + runner2Opcao);
					break;
				}
			}
			
			
			String betEncontrada = "\n\nMarketId R1 = " + marketCatalogueR1.getMarketId() + " market R1 = " + marketCatalogueR1.getMarketName() + " (opção: " + runner1Opcao + ")"
							+ "      x     " + "MarketId R2 = " + marketCatalogueR2.getMarketId() + " market R2 = " + marketCatalogueR2.getMarketName() + " (opção: " + runner2Opcao + ")" 
							+ "\n Event: " + marketCatalogueR1.getEvent() 
							+ "\nPorcentagem 1: " + porcentagem1 + " Porcentagem2: " + porcentagem2 +
							" stake1: " + stake1 + " stake2: " + stake2  + " odd1Size: " + odd1Size + " odd2Size: " + odd2Size;
			
			System.out.println("Preços R1: " + marketCatalogueR1.getMarketName() );
			for (PriceSize priceSize : runner1.getEx().getAvailableToBack()) {
				System.out.println("Preço: " + priceSize.getPrice() + " Size: " + priceSize.getSize());
			}
			
			System.out.println("Preços R2: " + marketCatalogueR2.getMarketName() );
			for (PriceSize priceSize : runner2.getEx().getAvailableToBack()) {
				System.out.println("Preço: " + priceSize.getPrice() + " Size: " + priceSize.getSize());
			}
			
			
			adicionaApostaNoCSV(betEncontrada);

			encontradosMaiorQueZero.add(betEncontrada);
			
			
//			String colunas = "dataEncontrado;marketId;evento;pais;dataEvento;market1;opcao1;market2;opcao2;odds1;odds2;stake1;stakeDisponivel1;stake2;stakeDisponivel2;lucro;porcentagem_stake_50";
			
			double lucroEmReais = round(porcentagem, 2);
			String linhaAposta = getDataEHoraAgora() 
					+ ";" + marketCatalogueR1.getMarketId() 
					+ ";" + marketCatalogueR1.getEvent().getName() 
					+ ";" + marketCatalogueR1.getEvent().getCountryCode()
					+ ";" + getDataFormatada(marketCatalogueR1.getEvent().getOpenDate())
					+ ";" + marketCatalogueR1.getMarketName() 
					+ ";" + runner1Opcao
					+ ";" + marketCatalogueR2.getMarketName() 
					+ ";" + runner2Opcao
					+ ";" + odd1
					+ ";" + odd2
					+ ";" + stake1
					+ ";" + odd1Size
					+ ";" + stake2
					+ ";" + odd2Size
					+ ";" + lucroEmReais
					+ ";" + (lucroEmReais * 100 / 50)
					;
			adicionaApostaNoCSVParaAuditoria(linhaAposta);
		}
		
    }
    
    
    
	private RunnerCatalog getRunnerCatalog(List<RunnerCatalog> runnerCatalogs, Runner runner) {
		RunnerCatalog runnerCatalogAtual = null;
		for (RunnerCatalog rc : runnerCatalogs) {
			if ( rc.getSelectionId().longValue() == runner.getSelectionId().longValue() ) {
				runnerCatalogAtual = rc;
				break;
			}
		}
		return runnerCatalogAtual;
	}
    

	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = BigDecimal.valueOf(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	

	private boolean apostar(MarketBook marketBook, Runner runner, Double stake, String customerRef)
			throws APINGException {
		boolean apostadoComSucesso = false;
		long selectionId = runner.getSelectionId();
//			                System.out.println("7. Place a bet below minimum stake to prevent the bet actually " +
//			                        "being placed for marketId: "+marketIdChosen+" with selectionId: "+selectionId+"...\n\n");
		List<PlaceInstruction> instructions = new ArrayList<PlaceInstruction>();
		PlaceInstruction instruction = new PlaceInstruction();
		instruction.setHandicap(0);
		instruction.setSide(Side.BACK);
		instruction.setOrderType(OrderType.LIMIT);

		LimitOrder limitOrder = new LimitOrder();
		limitOrder.setPersistenceType(PersistenceType.LAPSE);
		//API-NG will return an error with the default size=0.01. This is an expected behaviour.
		//You can adjust the size and price value in the "apingdemo.properties" file
		limitOrder.setPrice(runner.getLastPriceTraded());
		limitOrder.setSize(stake);

		instruction.setLimitOrder(limitOrder);
		instruction.setSelectionId(selectionId);
		instructions.add(instruction);

            
		String marketIdChosen = marketBook.getMarketId();
		PlaceExecutionReport placeBetResult = rescriptOperations.placeOrders(marketIdChosen , instructions, customerRef, applicationKey, sessionToken);

		// Handling the operation result
		if (placeBetResult.getStatus() == ExecutionReportStatus.SUCCESS) {
		    System.out.println("Your bet has been placed!!");
		    System.out.println(placeBetResult.getInstructionReports());
		    apostadoComSucesso = true;
		} else if (placeBetResult.getStatus() == ExecutionReportStatus.FAILURE) {
		    System.out.println("Your bet has NOT been placed :*( ");
		    System.out.println("The error is: " + placeBetResult.getErrorCode() + ": " + placeBetResult.getErrorCode().getMessage());
		}
		return apostadoComSucesso;
	}


    private void printMarketCatalogue(MarketCatalogue mk){
        System.out.println("Market Name: "+mk.getMarketName() + "; Id: "+mk.getMarketId()+"\n"
//        				+ "Esporte: " + mk.getEventType().getName() + " id: " + mk.getEventType().getId() + "\n"
        				+ "Data: " + mk.getEvent().getOpenDate());
        List<RunnerCatalog> runners = mk.getRunners();
        if(runners!=null){
            for(RunnerCatalog rCat : runners){
                System.out.println("Runner Name: "+rCat.getRunnerName()+"; Selection Id: "+rCat.getSelectionId()+"\n");
            }
        }
    }
    
    public void adicionaApostaNoCSV(String texto) {
		File file = new File ( "/home/henrique/betfair_log1.csv" );
        PrintWriter pw = null;
        try { 
        	FileWriter  csvOutputFile = new FileWriter (file, true);
	        pw = new PrintWriter(csvOutputFile);
	        pw.println(new Date() + " - " + texto);
        } catch (Exception e) {
			e.printStackTrace();
		} finally {
			if ( pw != null ) {
				pw.close();
			}
		}
    }
    
    public void adicionaApostaNoCSVParaAuditoria(String texto) {
    	if ( texto.contains("Double Chance")) {
    		encontradosMaiorQueZeroGeralDoubleChanceXMatchOdds++;
    	} else {
    		encontradosMaiorQueZeroGeral++;
    	}
    	File file = new File ( "/home/henrique/betfair_apostas_encontradas.csv" );
    	boolean arquivoExiste = file.exists();
    	
    	PrintWriter pw = null;
    	try { 
    		FileWriter  csvOutputFile = new FileWriter (file, true);
    		pw = new PrintWriter(csvOutputFile);
    		if ( !arquivoExiste ) {
    			String colunas = "dataEncontrado;marketId;evento;pais;dataEvento;market1;opcao1;market2;opcao2;odds1;odds2;stake1;stakeDisponivel1;stake2;stakeDisponivel2;lucro;porcentagem_stake_50";
    			pw.println(colunas);
    		}
    		pw.println(texto);
    	} catch (Exception e) {
    		e.printStackTrace();
    	} finally {
    		if ( pw != null ) {
    			pw.close();
    		}
    	}
    }
    
    public static void main(String args []) {
    	System.out.println("Próximo número aposta: " + getProximoNumeroAposta());
    	
    }
    
    private static Integer getProximoNumeroAposta() {
    	Integer ultimoNumAposta = getUltimoNumAposta();
    	ultimoNumAposta++;
    	setUltimoNumAposta(ultimoNumAposta.toString());
    	return ultimoNumAposta;
    }
    
    private static Integer getUltimoNumAposta() {
    	char[] array = new char[100];
    	
    	Integer resultado = null;

        try {
          // Creates a reader using the FileReader
          FileReader input = new FileReader("/home/henrique/betfair/numAposta.txt");

          // Reads characters
          input.read(array);
          System.out.println("Número");
          System.out.println(array);
          
          resultado = Integer.parseInt(new String(array).trim());
          
          // Closes the reader
          input.close();
        } catch(Exception e) {
          e.getStackTrace();
        }
        
        return resultado;	
    }
    

    
    public static void setUltimoNumAposta(String numAposta) {
		File file = new File ( "/home/henrique/betfair/numAposta.txt" );
        PrintWriter pw = null;
        try { 
        	FileWriter  csvOutputFile = new FileWriter (file, false);
	        pw = new PrintWriter(csvOutputFile);
	        pw.println(numAposta);
        } catch (Exception e) {
			e.printStackTrace();
		} finally {
			if ( pw != null ) {
				pw.close();
			}
		}
    }
    
    
    private String getDataEHoraAgora() {
    	return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
    }
    
    private String getDataFormatada(Date data) {
    	return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(data);
    }

}
