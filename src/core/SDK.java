package core;

import platform.Platform;

public class SDK {

	Platform platform;

	public SDK(String appKey, String appSecret, String server) {
		platform = new Platform(appKey, appSecret, server);
	}

	public Platform getPlatform() {
		return this.platform;
	}
}
