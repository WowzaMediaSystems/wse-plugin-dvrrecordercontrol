/**
 * Wowza server software and all components Copyright 2006 - 2015, Wowza Media Systems, LLC, licensed pursuant to the Wowza Media Software End User License Agreement.
 */
package com.wowza.wms.plugin;

import com.wowza.util.StringUtils;
import com.wowza.wms.application.IApplicationInstance;
import com.wowza.wms.logging.WMSLogger;
import com.wowza.wms.logging.WMSLoggerFactory;
import com.wowza.wms.module.ModuleBase;
import com.wowza.wms.stream.IMediaStream;
import com.wowza.wms.stream.livedvr.ILiveStreamDvrRecorderControl;

public class DvrRecorderControl extends ModuleBase
{

	private class DvrRecorderController implements ILiveStreamDvrRecorderControl
	{

		public boolean shouldDvrRecord(String recorderName, IMediaStream stream)
		{
			logger.info(MODULE_NAME + ".shouldDvrRecord [" + recorderName + " : " + stream.getName() + " : " + suffixes + "]", stream);
			if (suffixes.equals("*"))
			{
				logger.info(MODULE_NAME + ".shouldDvrRecord [" + recorderName + " : " + stream.getName() + " :  suffixes is wildcard. returning " + matchAllow + "]", stream);
				return matchAllow;
			}
			if (StringUtils.isEmpty(suffixes))
			{
				logger.info(MODULE_NAME + ".shouldDvrRecord [" + recorderName + " : " + stream.getName() + " : suffixes is empty. returning " + noMatchAllow + "]", stream);
				return noMatchAllow;
			}

			String[] suffixArray = suffixes.split(",");
			for (String suffix : suffixArray)
			{
				if (stream.getName().endsWith(suffix.trim()))
				{
					logger.info(MODULE_NAME + ".shouldDvrRecord [" + recorderName + " : " + stream.getName() + " : match found : " + suffix.trim() + " : returning " + matchAllow + "]", stream);
					return matchAllow;
				}
			}
			logger.info(MODULE_NAME + ".shouldDvrRecord [" + recorderName + " : " + stream.getName() + " : No match found : returning " + noMatchAllow + "]", stream);
			return noMatchAllow;
		}

	}

	public static final String MODULE_NAME = "DvrRecorderControl";
	public static final String PROP_NAME_PREFIX = "dvrRecorderControl";
	
	private WMSLogger logger;
	private String suffixes = "*";
	private boolean matchAllow = true;
	private boolean noMatchAllow = false;

	public void onAppStart(IApplicationInstance appInstance)
	{
		logger = WMSLoggerFactory.getLoggerObj(appInstance);
		
		suffixes = appInstance.getProperties().getPropertyStr(PROP_NAME_PREFIX + "Suffixes", suffixes);
		matchAllow = appInstance.getProperties().getPropertyBoolean(PROP_NAME_PREFIX + "MatchAllow", matchAllow);
		noMatchAllow = appInstance.getProperties().getPropertyBoolean(PROP_NAME_PREFIX + "NoMatchAllow", noMatchAllow);

		appInstance.setLiveStreamDvrRecorderControl(new DvrRecorderController());
	}
}