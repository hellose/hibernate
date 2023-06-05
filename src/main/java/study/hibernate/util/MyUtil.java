package study.hibernate.util;

public class MyUtil {

	public static boolean isSameIdentityHashCode(Object first, Object second) {
		if (System.identityHashCode(first) == System.identityHashCode(second)) {
			return true;
		}
		return false;
	}

	public static boolean isNull(Object obj) {
		if (obj == null) {
			return true;
		}
		return false;
	}

}
