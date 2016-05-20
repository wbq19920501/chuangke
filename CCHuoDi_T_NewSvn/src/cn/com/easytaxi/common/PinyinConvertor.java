package cn.com.easytaxi.common;
/**
 * <p>
 * Title: PinyinConvertor.java
 * </p>
 * <p>
 * Description: ����ƴ�����ַ���ȡ
 * </p>
 * <p>
 * Copyright:Copyright(c)2012
 * </p>
 * <p>
 * Company: CDSF
 * </p>
 * <p>
 * CreateTime:2012-8-22 ����09:10:20
 * </p>
 * 
 * @author XYW
 * @version 1.0
 * @bugs ��֧�ֶ����ִ���
 */
public class PinyinConvertor {

	// �������ĵı��뷶Χ��B0A1��45217��һֱ��F7FE��63486��
	private static int BEGIN = 45217;
	private static int END = 63486;

	// ������ ĸ��ʾ�����������GB2312�еĳ��ֵĵ�һ�����֣�Ҳ����˵�������Ǵ�������ĸa�ĵ�һ�����֡�
	// i, u, v��������ĸ, �Զ��������ǰ�����ĸ

	private static char[] chartable = { '��', '��', '��', '��', '��', '��', '��', '��',
			'��', '��', '��', '��', '��', '��', 'Ŷ', 'ž', '��', 'Ȼ', '��', '��', '��',
			'��', '��', '��', 'ѹ', '��' };

	// ��ʮ������ĸ�����Ӧ��ʮ�߸��˵�
	// GB2312�뺺������ʮ���Ʊ�ʾ
	private static int[] table = new int[27];

	// ��Ӧ����ĸ�����
	private static char[] initialtable = { 'A', 'B', 'C', 'D', 'E', 'F', 'G',
			'H', 'H', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
			'T', 'T', 'W', 'X', 'Y', 'Z' };

	// ��ʼ��
	static {
		for (int i = 0; i < 26; i++) {
			// �õ�GB2312�������ĸ����˵��ʮ����
			table[i] = gbValue(chartable[i]);
		}
		table[26] = END;// ������β
	}

	// ------------------------public������------------------------

	/**
	 * ����һ���������ֵ��ַ�������һ������ƴ������ĸ���ַ��� ����Ҫ��һ��������˼·���£�һ�����ַ����롢�жϡ����
	 * 
	 * @param sourceStr
	 * @param length
	 *            ���س��ȣ�Ϊ0ʱֻ���ص�һ����������ĸ��Ϊ1ʱ����ÿ�����ֵ�����ĸ��
	 * @return
	 */
	public static String cn2py(String sourceStr, int length) {
		if(sourceStr == null || sourceStr.equals("")){
			return "#";
		}
		
		String result = "";
		int StrLength = sourceStr.length();
		try {
			for (int i = 0; i < StrLength; i++) {
				result += char2InitialSpecial(sourceStr.charAt(i));
			}
		} catch (Exception e) {
			result = "#";
		}

		if (length == 0) {
			return result.length() > 1 ? result.substring(0, 1) : result;
		} else {
			return result;
		}
	}

	// ------------------------private������------------------------

	/**
	 * �����ַ�,�õ�������ĸ,Ӣ����ĸ���ض�Ӧ�Ĵ�д��ĸ,�����Ǽ��庺�ַ��� '#'
	 */
	private static char char2InitialSpecial(char ch) {

		// ��Ӣ����ĸ�Ĵ���Сд��ĸת��Ϊ��д����д��ֱ�ӷ���
		if (ch >= 'a' && ch <= 'z')
			return (char) (ch - 'a' + 'A');
		if (ch >= 'A' && ch <= 'Z')
			return ch;
		// �Է�Ӣ����ĸ�Ĵ���ת��Ϊ����ĸ��Ȼ���ж��Ƿ������Χ�ڣ�
		// �����ǣ���ֱ�ӷ��ء�
		// ���ǣ���������ڵĽ����жϡ�
		int gb = gbValue(ch);// ����ת������ĸ
		// ���������֮ǰ��ֱ�ӷ���
		if ((gb < BEGIN) || (gb > END)) {
			return '#';
		}

		int i;
		for (i = 0; i < 26; i++) {// �ж�ƥ��������䣬ƥ�䵽��break,�ж��������硰[,)��
			if ((gb >= table[i]) && (gb < table[i + 1]))
				break;
		}

		if (gb == END) {// ����GB2312�������Ҷ�
			i = 25;
		}

		return initialtable[i]; // ����������У���������ĸ
	}

	/**
	 * 
	 * ȡ�����ֵı��룬��һ�����֣�GB2312��ת��Ϊʮ���Ʊ�ʾ
	 */
	private static int gbValue(char ch) {
		String str = new String();
		str += ch;
		try {
			byte[] bytes = str.getBytes("GB2312");
			if (bytes.length < 2)
				return 0;
			return (bytes[0] << 8 & 0xff00) + (bytes[1] & 0xff);
		} catch (Exception e) {
			return 0;
		}
	}
}
