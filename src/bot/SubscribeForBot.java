package bot;

import java.io.IOException;

import platform.Platform;
import subscription.Subscription;

public class SubscribeForBot {

	void subscribe(Platform p) {
		final Subscription sub = new Subscription(p);

		String[] s = { "/restapi/v1.0/account/~/extension/~/glip/groups",
				"/restapi/v1.0/account/~/extension/~/glip/posts" };

		sub.addEvents(s);

		try {
			sub.subscribe();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
