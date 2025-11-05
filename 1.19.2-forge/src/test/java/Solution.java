import java.util.*;

class Solution {
	public static void main(String[] args) {
		System.out.println(new Solution().longestPalindrome("babad"));
	}
	public String longestPalindrome(String s) {
		int[] ret = new int[s.length() * 2 + 10];
		for (int i = 0; i < ret.length; i++) {
			if (i % 2 == 1) {
				ret[i] = 1;
			}
		}
		Map<Character, List<Integer>> map = new HashMap<>();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (map.containsKey(c)) {
				for (Integer left : map.get(c)) {
					int len = i - left + 1;
					if (ret[i + left + 1] == len - 2) {
						ret[i + left + 1] = len;
					}
				}
			}
			map.computeIfAbsent(c, k -> new ArrayList<>()).add(i);
		}
		int max = 0;
		int index = 0;
		for (int i = 0, retLength = ret.length; i < retLength; i++) {
			int val = ret[i];
			if (val > max) {
				max = val;
				index = i;
			}
		}
		int left = (index - max) / 2;
		return s.substring(left, left + max);
	}
}