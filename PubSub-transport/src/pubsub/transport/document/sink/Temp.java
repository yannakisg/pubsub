/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pubsub.transport.document.sink;

/**
 *
 * @author gaspar
 */
public class Temp {
    public static void main(String args[])
    {
        String cardNumber = "4057840101429770";
        int len = cardNumber.length();
        int oddoeven = len & 1;
        int sum = 0;
        
        for (int i = 0; i < len; i++)
        {
            int digit = Integer.parseInt(Character.toString(cardNumber.charAt(i)));
            if (((i & 1) ^ oddoeven) != 0) {
                digit *= 2;
                if (digit > 9)
                {
                    digit -= 9;
                }
            }
            sum += digit;
        }
        
        System.out.println(sum);
    }
}

/*
 * var no_digit = argvalue.length;
    var oddoeven = no_digit & 1;
    var sum = 0;
    
    for (var count = 0; count < no_digit; count++) {
	var digit = parseInt(argvalue.charAt(count));
	if (!((count & 1) ^ oddoeven)) {
	    digit *= 2;
	    if (digit > 9)
		digit -= 9;
	}
	sum += digit;
    }

    if (sum % 10 == 0)
	return true;
    else
	return false;
 */