package com.trading.cache;

import java.util.ArrayList;
import java.util.List;

import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.trading.model.OIObject;

@Component
public class OptionDataCache {

	private static final String OPTION_DATA_DATE_TIME = "OPTION_DATA_DATE_TIME";
	private static final Logger LOGGER = LoggerFactory.getLogger(OptionDataCache.class);

	@Autowired
	private RedissonClient redissonClient;

	public List<OIObject> getOptionDate(String date) {
		try {
			RMap<String, List<OIObject>> dateTimeToOption = redissonClient.getMap(OPTION_DATA_DATE_TIME);
			return dateTimeToOption.get(date);
		} catch (Exception e) {
			LOGGER.error("Error in getOptionDate() method !!", e);
		}
		return null;
	}

	public void saveOIObject(OIObject OIObject,String date) {
		RMap<String, List<OIObject>> rMapOIObject = redissonClient.getMap(OPTION_DATA_DATE_TIME);

		List<OIObject> list = rMapOIObject.get(date);
		if (list == null) {
			list = new ArrayList<OIObject>();
		}
		list.add(OIObject);
		rMapOIObject.put(date, list);
	}
}
