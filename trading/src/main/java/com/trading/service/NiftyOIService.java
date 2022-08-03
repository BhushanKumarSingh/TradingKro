package com.trading.service;

import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.trading.cache.OptionDataCache;
import com.trading.model.OIObject;
import com.trading.model.OptionData;

import reactor.core.publisher.Flux;

@Service
public class NiftyOIService {

	public static final String BLACK_BOLD = "\033[1;30m"; // BLACK
	public static final String RED_BOLD = "\033[1;31m"; // RED
	public static final String GREEN_BOLD = "\033[1;32m"; // GREEN
	public static final String BLACK = "\033[0;30m";

	@Autowired
	OptionDataCache optionDataCache;
	
	@Autowired
	SimpMessagingTemplate messagingTemplate; 

	@SuppressWarnings("unchecked")
	public Map<String, Object> oiData() {
		System.out.println("======================================================================================");
		WebClient webClient = WebClient.builder()
				.baseUrl("https://www.nseindia.com/api/option-chain-indices?symbol=NIFTY")
				.defaultHeader("user-agent",
						"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36 Edg/91.0.864.59")
				.defaultHeader("accept-language", "en,gu;q=0.9,hi;q=0.8")
				.defaultHeader("accept-encoding", "gzip,deflate,br").build();
		Flux<String> resp = webClient.get().retrieve().bodyToFlux(DataBuffer.class).map(buffer -> {
			String string = buffer.toString(Charset.forName("UTF-8"));
			DataBufferUtils.release(buffer);
			return string;
		});

		List<String> myStringList = resp.collectList().block();

//		System.out.println(myStringList.size());
		String finalString = "";
		for (int i = 0; i < myStringList.size(); i++) {
			finalString += myStringList.get(i);
		}

		try {

			// convert JSON string to Java Map
			Map<String, Object> map = new ObjectMapper().readValue(finalString, Map.class);

			double underlyingValue =  (double) ((Object) ((Object)((Map<String, Object>) map.get("records")).get("underlyingValue")));

			List<String> expiryList = (List<String>) ((Map<String, Object>) map.get("records")).get("expiryDates");
//			System.out.println(expiryList.get(0));

			DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");

			Date expDate = formatter.parse(expiryList.get(0));
			String todayString = formatter.format(new Date());
			Date today = formatter.parse(todayString);
			long duration = expDate.getTime() - today.getTime();

			long diffInDays = TimeUnit.MILLISECONDS.toDays(duration);
//			System.out.println("diffInDays " + diffInDays);

			System.out.println(((Map<String, Object>) map.get("records")).get("timestamp"));

//			System.out.println("Strike " + BLACK_BOLD + underlyingValue);

			int strike = (int) underlyingValue / 50;
			strike = (strike + 1) * 50;
//			System.out.println(strike);

			int currentStrike = ((strike - underlyingValue) >= 25 ? strike - 50 : strike);
//			System.out.println(GREEN_BOLD + currentStrike);
			List<Map<String, Object>> dataList = (List<Map<String, Object>>) ((Map<String, Object>) map.get("filtered"))
					.get("data");
			List<OptionData> optionDatas = new ArrayList<>();
			int sumOfPe = 0;
			int sumOfCe = 0;
			int sumOfCeOi = 0;
			int sumOfPeOi = 0;
			for (Map<String, Object> data : dataList) {
				OptionData optionData = new OptionData();
				int val = (int) data.get("strikePrice");
				int changeinOpenInterestCe =  (int)((Map<String, Object>) data.get("CE")).get("changeinOpenInterest") *50;
				int changeinOpenInterestPe = (int) ((Map<String, Object>) data.get("PE")).get("changeinOpenInterest")*50;
				int openInterestCe = (int) ((Map<String, Object>) data.get("CE")).get("openInterest")*50;
				int openInterestPe = (int) ((Map<String, Object>) data.get("PE")).get("openInterest") *50;

				sumOfCeOi += openInterestCe;
				sumOfPeOi += openInterestPe;

				Object lastPriceCe =((Map<String, Object>) data.get("CE")).get("lastPrice");
				Object lastPricePe = ((Map<String, Object>) data.get("PE")).get("lastPrice");

				int upperRange = val + 300;
				int lowerRange = val - 300;

				if (diffInDays <= 2) {
					upperRange = val + 200;
					lowerRange = val - 200;
				}

				if ((upperRange >= currentStrike) && (currentStrike >= lowerRange)) {
					optionData.setStrikePrice((int) data.get("strikePrice"));
					optionData.setChangeInOiCe(changeinOpenInterestCe);
					optionData.setChangeInOiPe(changeinOpenInterestPe);
					optionData.setLastPriceCe(lastPriceCe);
					optionData.setLastPricePe(lastPricePe);
					optionData.setOpenInterestCe(openInterestCe);
					optionData.setOpenInterestPe(openInterestPe);

					sumOfCe += changeinOpenInterestCe;
					sumOfPe += changeinOpenInterestPe;
					

					
				}

			}
			
			Map<String, Object> response = new HashMap<>();
			response.put("strikePrice", currentStrike);
			response.put("totalCeOi", sumOfCe);
			response.put("totalPeOi", sumOfPe);
			response.put("data", optionDatas);
			response.put("sumofCeOi", sumOfCeOi);
			response.put("sumofPeOi", sumOfPeOi);
			
			DateFormat form = new SimpleDateFormat("dd-MMM-yyyy hh:mm");
			DateFormat form1 = new SimpleDateFormat("dd-MMM-yyyy");
			OIObject oiObject = new OIObject();
			String todayStr = form.format(new Date());
			String date = form1.format(new Date());
			
			oiObject.setDateTime(todayStr);
			oiObject.setChnageInOiCE(""+sumOfCe);
			oiObject.setChnageInOiPE(""+sumOfPe);
			oiObject.setOiCE(""+sumOfCeOi);
			oiObject.setOiPE(""+sumOfPeOi);
			oiObject.setStrike(""+underlyingValue);
			
			optionDataCache.saveOIObject(oiObject, date);
//			optionDatas.add(oiObject);
			
			
			
//			if (sumOfCe > sumOfPe) {
//				System.out.println("Change in OI CE: " + RED_BOLD + String.format("%,d", sumOfCe));
//				System.out.println("Change in OI PE: " + RED_BOLD + String.format("%,d", sumOfPe));
//				System.out.println("OI CE: " + RED_BOLD + String.format("%,d", sumOfCeOi));
//				System.out.println("OI PE: " + RED_BOLD + String.format("%,d", sumOfPeOi));
//			} else {
//				System.out.println("Change in OI CE: " + GREEN_BOLD + String.format("%,d", sumOfCe));
//				System.out.println("Change in OI PE: " + GREEN_BOLD + String.format("%,d", sumOfPe));
//				System.out.println("OI CE: " + GREEN_BOLD + String.format("%,d", sumOfCeOi));
//				System.out.println("OI PE: " + GREEN_BOLD + String.format("%,d", sumOfPeOi));
//			}
//			
			List<OIObject> lis = optionDataCache.getOptionDate(date);
//			System.out.println(todayStr);
//			for(OIObject op : lis)
//				System.out.println(op.toString());
			
			
			//System.out.printf(BLACK_BOLD+"%20s %20s %20s %20s %20s %20s %20s", "Date/Time", "OI CE", "OI PE", "Strike", "Change OI CE","Diff","Change OI PE");
		    //System.out.println();
		    //System.out.println("----------------------------------------------------------------------------------------------------------------------------");
		    double strikePrice = 0;
		    String message = "";
		    for(OIObject op: lis){
//		    	
		    	String strikeColor = strikePrice < Double.parseDouble(op.getStrike()) ? GREEN_BOLD : RED_BOLD;
		    	strikePrice = Double.parseDouble(op.getStrike());
		    	
		    	int diff = Integer.parseInt(op.getChnageInOiPE()) - Integer.parseInt(op.getChnageInOiCE());
		    	String color = diff>0?GREEN_BOLD:RED_BOLD;
//		        System.out.format(BLACK_BOLD+"%20s"+BLACK+" %20s %20s "+strikeColor+"%20s"+BLACK+" %20s "+color+"%20d"+BLACK+" %20s",
//		                op.getDateTime(), op.getOiCE(), op.getOiPE(), op.getStrike(), op.getChnageInOiCE(),diff,op.getChnageInOiPE());
//		        System.out.println();
		    	
		    	 message = String.format(BLACK_BOLD+"%20s"+BLACK+" %20s %20s "+strikeColor+"%20s"+BLACK+" %20s "+color+"%20d"+BLACK+" %20s",
		                op.getDateTime(), op.getOiCE(), op.getOiPE(), op.getStrike(), op.getChnageInOiCE(),diff,op.getChnageInOiPE());
		    	System.out.println(message);
		    	
		    }
		    Greeting greeting = new Greeting(message);
		    
		    System.out.println(lis);
		    messagingTemplate.convertAndSend("/topic/greetings", lis);
			return null;

		} catch (Exception ex) {
//			ex.printStackTrace();
		}
		return null;
	}

}
