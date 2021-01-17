import javax.swing.plaf.synth.SynthTextAreaUI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;


public class BigInt {
    public ArrayList<Integer> number;

    private int maxInteger= 1000000000;

    /**
     * Constructor
     * @param number
     */
    BigInt(String number){
        //Initialize arraylist
        this.number = new ArrayList<Integer>();

        //Get number of bloc of 9 digit
        int n = number.length()/9;
        //Get remainder
        int r = number.length()%9;
        //Reverse number to deal with low digits first
        String strReverse = new StringBuilder(number).reverse().toString();
        //Make bloc of 9 digits to store in int value
        for(int i=0; i<n; i++){
            String temp = "";
            for(int j=0; j<9; j++){
                temp+=strReverse.charAt(i*9+j);
            }
            int arrayCase = Integer.parseInt(new StringBuilder(temp).reverse().toString());
            //Add int to array list
            this.number.add(arrayCase);
        }
        //Add remainder digits
        if(r!=0){
            String temp="";
            for(int i=0; i<r; i++){
                temp+=strReverse.charAt(n*9 + i);
            }
            int arrayCase = Integer.parseInt(new StringBuilder(temp).reverse().toString());
            this.number.add(arrayCase);
        }
        //Reverse array list because low digits are at the beginning
        Collections.reverse(this.number);
    }

    BigInt(ArrayList<Integer> number){
        this.number = number;
    }

    public BigInt addNumber(BigInt numberToAdd){
        //This.number is the largest number
        if(numberToAdd.number.size() > this.number.size()){
            ArrayList<Integer> temp = this.number;
            this.number = numberToAdd.number;
            numberToAdd.number = temp;
        }

        //Store length of each number
        int length1 = this.number.size();
        int length2 = numberToAdd.number.size();
        int diff = length1-length2;

        //Empty array list to store the result
        ArrayList<Integer> arrayResult = new ArrayList<Integer>();

        //Carry initialized to 0
        int carry = 0;

        //Go through arraylist from back with the shortest number length as ref
        for(int i=length2-1; i>=0;i--){
            int sum = this.number.get(i + diff) + numberToAdd.number.get(i) + carry;

            if(sum >= maxInteger){
                String strSum = String.valueOf(sum);
                String temp ="";
                for(int j=0; j<(strSum.length() -9); j++){
                    temp += strSum.charAt(j);
                }
                carry= Integer.parseInt(temp);
                sum = sum - maxInteger;
            }
            else{
                carry = 0;
            }
            arrayResult.add(sum);
        }

        //Add remaining digits of first number
        for(int i= diff-1; i>=0;i--){
            int sum = this.number.get(i) + carry;
            if(sum >= maxInteger){
                carry = sum - maxInteger;
                sum = sum - maxInteger;
            }
            else{
                carry = 0;
            }
            arrayResult.add(sum);
        }

        //Add remaining carry if there is
        if(carry > 0){
            arrayResult.add(carry);
        }

        Collections.reverse(arrayResult);
        return new BigInt(arrayResult);
    }

    public BigInt subtractNumber(BigInt numberToSubtract){

        //Check if number to subtract is greater than current number
        if(this.isSmaller(numberToSubtract)){
            ArrayList<Integer> temp = this.number;
            this.number = numberToSubtract.number;
            numberToSubtract.number = temp;
        }

        int length1 = this.number.size();
        int length2 = numberToSubtract.number.size();
        int length_diff = length1  - length2;

        //Empty arraylist to store the result
        ArrayList<Integer> resultArray = new ArrayList<Integer>();

        //Reverse both of arraylist
        Collections.reverse(this.number);
        Collections.reverse(numberToSubtract.number);

        //Initialize carry to 0
        int carry = 0;

        //Go through array with shortest array list length
        for(int i=0; i<length2; i++){
            int diff = this.number.get(i) - numberToSubtract.number.get(i) - carry;
            if(diff <0){
                diff += maxInteger;
                carry = 1;
            }
            else{
                carry = 0;
            }
            resultArray.add(diff);
        }

        //Add digits of first number remaining
        for(int i=length2; i<length1; i++){
            int diff = this.number.get(i) - carry;

            if(diff < 0){
                diff += maxInteger;
                carry = 1;
            }
            else{
                carry = 0;
            }
            resultArray.add(diff);
        }

        //Reverse array list
        Collections.reverse(resultArray);
        Collections.reverse(this.number);
        Collections.reverse(numberToSubtract.number);

        int index = 0;
        while(index < resultArray.size() && resultArray.get(index) == 0){
            resultArray.remove(index);
        }
        //Check if result array is full of 0
        if(index == resultArray.size()){
            return new BigInt("0");
        }

        return new BigInt(resultArray);
    }

    public BigInt multiplyNumber(BigInt numberToMultiply){

        //Get size of each number
        int length1 = this.number.size();
        int length2 = numberToMultiply.number.size();

        //Array to final result
        ArrayList<Integer> resultArray = new ArrayList<Integer>();

        //Intermediate result array
        long interResult[] = new long[length1 + length2];

        //Index to go through result for each digit of first number
        int index1 = 0;

        //Index to go through result for each digit of second number
        int index2 = 0;

        //Go through first number from back
        for(int i=length1 - 1; i>=0; i--){

            //Reset carry to 0
            long carry = 0;

            //reset position of index2 for each loop on first number
            index2 = 0;

            //Go through second number from back
            for(int j=length2-1; j>=0; j--){


                //Multiply two digit, add result stored at current indexes and carry
                long product = (long)this.number.get(i) * numberToMultiply.number.get(j) + interResult[index1 + index2] + carry;
                //Get new carry
                carry = product / maxInteger;

                //Store result
                interResult[index1 + index2]= product % maxInteger;
                //Increase index2 because next digit of number2
                index2++;
            }
            //After last digit of number2, store carry in the result array
            if(carry > 0){
                interResult[index1 + index2] +=carry;
            }

            //Increase index1 because next digit of number1
            index1++;
        }

        //Remove 0 from the right in result array
        int index = interResult.length-1;
        while(index >=0 && interResult[index] == 0){
            index--;
        }

        //Check if result array is full of 0
        if(index == -1){
            return new BigInt("0");
        }

        //Get each result digit and store in final result
        while(index >=0){
            resultArray.add((int)interResult[index--]);
        }


        return new BigInt(resultArray);
    }

    public BigInt addModulusNumber(BigInt numberToAdd, BigInt modulus){
        BigInt newNumber= new BigInt(this.number);
        while(newNumber.isSmaller(modulus) == false){
            newNumber = newNumber.subtractNumber(modulus);
        }

        BigInt newNumberToAdd = new BigInt(numberToAdd.number);
        while (newNumberToAdd.isSmaller(modulus) == false){
            newNumberToAdd = newNumberToAdd.subtractNumber(modulus);
        }
        BigInt sum = newNumber.addNumber(newNumberToAdd);

        while(sum.isSmaller(modulus) == false){
           sum = sum.subtractNumber(modulus);
        }
        return sum;
    }

    public BigInt subtractModulusNumber(BigInt numberToSubtract, BigInt modulus){
        if(!this.isSmaller(numberToSubtract)) {
            BigInt diff = this.subtractNumber(numberToSubtract);

            while (diff.isSmaller(modulus) == false) {
                diff = diff.subtractNumber(modulus);
            }
            return diff;
        }
        else{
            BigInt interSum = this.addNumber(modulus);
            BigInt diff = interSum.subtractNumber(numberToSubtract);
            return diff;
        }
    }

    public boolean isSmaller(BigInt numberToCompare){

        if(this.number.size() < numberToCompare.number.size()){
            return true;
        }
        if(this.number.size() > numberToCompare.number.size()){
            return false;
        }

        for(int i=0; i<this.number.size();i++){
            if(this.number.get(i) < numberToCompare.number.get(i)){
                return true;
            }
            else if(this.number.get(i) > numberToCompare.number.get(i)){
                return false;
            }
        }
        return false;
    }

    public BigInt multiplyModulusNumber(BigInt numberToMultiply, BigInt modulus){
        BigInt newNumber= new BigInt(this.number);
        while(newNumber.isSmaller(modulus) == false){
            newNumber = newNumber.subtractNumber(modulus);
        }

        BigInt newNumberToMultiply = new BigInt(numberToMultiply.number);
        while (newNumberToMultiply.isSmaller(modulus) == false){
            newNumberToMultiply = newNumberToMultiply.subtractNumber(modulus);
        }
        BigInt product = newNumber.multiplyNumber(newNumberToMultiply);

        while(product.isSmaller(modulus) == false){
            product = product.subtractNumber(modulus);
        }
        return product;
    }

    public BigInt montgomeryMultiply(BigInt numberToMultiply, BigInt modulus, BigInt auxiliaryModulus, int powerAuxiliaryModulus, BigInt invOppositeModulus){
        BigInt S = this.multiplyNumber(numberToMultiply);

        BigInt T = S.multiplyModulusNumber(invOppositeModulus, auxiliaryModulus);

        BigInt interM= T.multiplyNumber(modulus);
        BigInt M = S.addNumber(interM);

        System.out.println(M.number.size());
        System.out.println(M.number);

        return new BigInt("0");
    }

    public BigInt convertToMontgomeryRepresentation(BigInt modulus, BigInt auxiliaryModulus){
        BigInt result = this.multiplyModulusNumber(auxiliaryModulus,modulus);
        return result;
    }

    public BigInt kRightShift(int k){
        for(int i=0; i<this.number.size(); i++){
            for(int j= 0; j<k; j++){

            }
        }
        return new BigInt("0");
    }
    public static void main(String []args){
        String num1 = "200894948979040927328258228709125626775654384151035399430126149634155951726555656530836619016851401181634119075195306560556522333890144748569417236576112898413971241738507921618160678020614533064698364979058491096498384432155243326128811022681496149676643807183628103956224850302032242635887510638832800585061";
        String num2 = "629771257101315092232556890504698430287522739827399921702154314612132242424506679018833168190835013601107873278918794261814058092181436200519603529875036065179261465359321247408025904993359159662396458873647450196654148434784236955421991157216811330627991204102187002626767800901267034610546596105702879946560";
        String mod = "179769313486231590772930519078902473361797697894230657273430081157732675805500963132708477322407536021120113879871393357658789768814416622492847430639474124377767893424865485276302219601246094119453082952085005768838150682342462881473913110540827237163350510684586298239947245938479716304835356329624581028101";
        String r = "359538626972463181545861038157804946723595395788461314546860162315465351611001926265416954644815072042240227759742786715317579537628833244985694861278948248755535786849730970552604439202492188238906165904170011537676301364684925762947826221081654474326701021369172596479894491876959432609670712659248448274432";
        String v = "80165606214706358888262949260895688660806166397163882131250855912340942766649664287563763530431913426554394774518322343768315312549972677838115892448936849697264139898297561566880023739815324475017014134115227509042189864138689428985899464162789445460525579558726007047188788599384685228079781371689950718925";
        BigInt bigint1 = new BigInt(num1);
        BigInt bigint2 = new BigInt(num2);
        BigInt modulus = new BigInt(mod);
        BigInt auxiliaryModulus = new BigInt(r);
        BigInt invOppositeModulus = new BigInt(v);

        BigInt test1 = new BigInt("40439259280043854394384724185215749617014043121956053583069354910110521930087897686954214880699127781861079392681714927097809127646849818195762273579332978298089179502487838235347910285752692409785787478420483372749541044551245805611185128588941837082731512683451837605726025272724743514315790484732647013879");
        BigInt test2 = new BigInt("14761283389341084194002215078975668519400256859157264982040334961059740939613021614698223379747222083756600079268157839569913528455569682736184083845992508723353976741692130256045632959847424355650571301381751510405049722072263914344963486309696781267160573814092959220315966655838299897980514271971826055673");
        BigInt resulttest = test1.multiplyNumber(test2);
        System.out.println(resulttest.number);

        System.out.println(bigint1.isSmaller(modulus));
        System.out.println(bigint2.isSmaller(modulus));

        /*System.out.println("Test of add function");
        BigInt addResult = bigint1.addNumber(bigint2);
        BigInt expectedAddResult = new BigInt("359538626972463181545861038157804946723595395788461314546860162315465351611001926265416954644815072042240227759742786715317579537628833244985694861278948248755535786849730970552604439202492188238906165904170011537676301364684925762947826221081654474326701021369172596479894491876959431662373220919845708945103");
        if(addResult.number.equals(expectedAddResult.number)== true){
            System.out.println("Success: result equals the expected result");
        }
        else{
            System.out.println("Failure: result is not equal to expected result");
        }

        System.out.println("Test of subtract function");
        BigInt subResult = bigint1.subtractNumber(bigint2);
        BigInt expectedSubResult = new BigInt("947297491739402739329329");
        if(subResult.number.equals(expectedSubResult.number)){
            System.out.println("Success: result equals the expected result");
        }
        else{
            System.out.println("Failure: result is not equal to expected result");
        }

        System.out.println("Test of multiply function");
        BigInt multResult = bigint1.multiplyNumber(bigint2);
        BigInt expectedMultResult = new BigInt("32317006071311007300714876688669951960444102669715484032130345427524655138867890893197201411522913463688717960921898019494119559150490921095088152386448283120630877367300996091750197750389652106796057638384067568276792218642619756161838094338476170470581645852036305042887575891541065638312532641902358562313557645678607871804709674682251122437484995835225220569324713979702626433156199775675034187892365664197561165869665170221052728358528070536301506267137060681139646113707021801781900342550100751340170242695239252631457797132388894514558334405045536654867218521663744160206809922800549021575798311372714087022592");
        if(multResult.number.equals(expectedMultResult.number)){
            System.out.println("Success: result equals the expected result");
        }
        else{
            System.out.println("Failure: result is not equal to expected result");
            }

        System.out.println("Test of add with modulus function");
        BigInt addModulusResult = bigint1.addModulusNumber(bigint2, modulus);
        BigInt expectedAddModulusResult = new BigInt("89884656743115795386465259539451236680898848947115328636715040578866337902750481566354238661203768010560056939935696678829394884407208311246423715319737062188883946712432742638151109800623047059726541476042502884419075341171231440736956555270413618581675255342293149119973622969239857205120186425409372739279");
        if(addModulusResult.number.equals(expectedAddModulusResult.number)){
            System.out.println("Success: result equals the expected result");
        }
        else{
            System.out.println("Failure: result is not equal to expected result");
        }

        System.out.println("Test of subtract modulus function with a>b");
        BigInt subModulusResult = bigint1.subtractModulusNumber(bigint2,modulus);
        BigInt expectedSubModulusResult = new BigInt("947297491739402739329329");
        if(subModulusResult.number.equals((expectedSubModulusResult.number))){
            System.out.println("Success: result equals the expected result");
        }
        else{
            System.out.println("Failure: result is not equal to expected result");
            System.out.println(subModulusResult.number);
            System.out.println(expectedSubModulusResult.number);
        }

        System.out.println("Test of subtract modulus function with a<b");
        BigInt subModulusResult2 = bigint2.subtractModulusNumber(bigint1,modulus);
        BigInt expectedSubModulusResult2 = new BigInt("89884656743115795386465259539451236680898848947115328636715040578866337902750481566354238661203768010560056939935696678829394884407208311246423715319737062188883946712432742638151109800623047059726541476042502884419075341171231440736956555270413618581675255342293149119973622969239857205120186425409372739279");
        if(subModulusResult2.number.equals((expectedSubModulusResult2.number))){
            System.out.println("Success: result equals the expected result");
        }
        else{
            System.out.println("Failure: result is not equal to expected result");
        }*/

        System.out.println("Test of montgomery multiplication function");
        BigInt newBigInt1 = bigint1.convertToMontgomeryRepresentation(modulus,auxiliaryModulus);
        BigInt newBigInt2 = bigint2.convertToMontgomeryRepresentation(modulus, auxiliaryModulus);
        BigInt result = newBigInt1.montgomeryMultiply(newBigInt2, modulus, auxiliaryModulus,1025, invOppositeModulus);
    }
}


