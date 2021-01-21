import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.function.BinaryOperator;


public class BigInt {
    public ArrayList<Integer> number;

    private int maxInteger = 1000000000;

    BigInt() {
        this.number = new ArrayList<Integer>();
    }


    /**
     * Constructor
     *
     * @param number
     */
    BigInt(String number) {
        //Initialize arraylist
        this.number = new ArrayList<Integer>();
        //Get number of bloc of 9 digit
        int n = number.length() / 9;
        //Get remainder
        int r = number.length() % 9;
        //Reverse number to deal with low digits first
        String strReverse = new StringBuilder(number).reverse().toString();
        //Make bloc of 9 digits to store in int value
        for (int i = 0; i < n; i++) {
            String temp = "";
            for (int j = 0; j < 9; j++) {
                temp += strReverse.charAt(i * 9 + j);
            }
            int arrayCase = Integer.parseInt(new StringBuilder(temp).reverse().toString());
            //Add int to array list
            this.number.add(arrayCase);
        }
        //Add remainder digits
        if (r != 0) {
            String temp = "";
            for (int i = 0; i < r; i++) {
                temp += strReverse.charAt(n * 9 + i);
            }
            int arrayCase = Integer.parseInt(new StringBuilder(temp).reverse().toString());
            this.number.add(arrayCase);
        }
        //Reverse array list because low digits are at the beginning
        Collections.reverse(this.number);
    }

    BigInt(ArrayList<Integer> number) {
        this.number = number;
    }

    public BigInt randomBigNumber() {
        //Set max limit to 2^1025
        BigInteger maxLimit = new BigInteger("89884656743115795386465259539451236680898848947115328636715040578866337902750481566354238661203768010560056939935696678829394884407208311246423715319737062188883946712432742638151109800623047059726541476042502884419075341171231440736956555270413618581675255342293149119973622969239858152417678164812112068608");
        //Set min limit to 2^1024
        BigInteger minLimit = new BigInteger("44942328371557897693232629769725618340449424473557664318357520289433168951375240783177119330601884005280028469967848339414697442203604155623211857659868531094441973356216371319075554900311523529863270738021251442209537670585615720368478277635206809290837627671146574559986811484619929076208839082406056034304");

        BigInteger diff = maxLimit.subtract(minLimit);
        Random randNum = new Random();
        int len = maxLimit.bitLength();
        BigInteger result = new BigInteger(len, randNum);

        if (result.compareTo(minLimit) < 0) {
            result = result.add(minLimit);
        }
        if (result.compareTo(diff) >= 0) {
            result = result.mod(diff).add(minLimit);
        }
        return new BigInt(result.toString());
    }

    public BigInt addNumber(BigInt numberToAdd) {
        //This.number is the largest number
        if (numberToAdd.number.size() > this.number.size()) {
            ArrayList<Integer> temp = this.number;
            this.number = numberToAdd.number;
            numberToAdd.number = temp;
        }

        //Store length of each number
        int length1 = this.number.size();
        int length2 = numberToAdd.number.size();
        int diff = length1 - length2;

        //Empty array list to store the result
        ArrayList<Integer> arrayResult = new ArrayList<Integer>();

        //Carry initialized to 0
        int carry = 0;

        //Go through arraylist from back with the shortest number length as ref
        for (int i = length2 - 1; i >= 0; i--) {
            int sum = this.number.get(i + diff) + numberToAdd.number.get(i) + carry;

            if (sum >= maxInteger) {
                String strSum = String.valueOf(sum);
                String temp = "";
                for (int j = 0; j < (strSum.length() - 9); j++) {
                    temp += strSum.charAt(j);
                }
                carry = Integer.parseInt(temp);
                sum = sum - maxInteger;
            } else {
                carry = 0;
            }
            arrayResult.add(sum);
        }

        //Add remaining digits of first number
        for (int i = diff - 1; i >= 0; i--) {
            int sum = this.number.get(i) + carry;
            if (sum >= maxInteger) {
                carry = sum - maxInteger;
                sum = sum - maxInteger;
            } else {
                carry = 0;
            }
            arrayResult.add(sum);
        }

        //Add remaining carry if there is
        if (carry > 0) {
            arrayResult.add(carry);
        }

        Collections.reverse(arrayResult);
        return new BigInt(arrayResult);
    }

    public BigInt subtractNumber(BigInt numberToSubtract) {

        //Check if number to subtract is greater than current number
        if (this.isSmaller(numberToSubtract)) {
            ArrayList<Integer> temp = this.number;
            this.number = numberToSubtract.number;
            numberToSubtract.number = temp;
        }

        int length1 = this.number.size();
        int length2 = numberToSubtract.number.size();
        int length_diff = length1 - length2;

        //Empty arraylist to store the result
        ArrayList<Integer> resultArray = new ArrayList<Integer>();

        //Reverse both of arraylist
        Collections.reverse(this.number);
        Collections.reverse(numberToSubtract.number);

        //Initialize carry to 0
        int carry = 0;

        //Go through array with shortest array list length
        for (int i = 0; i < length2; i++) {
            int diff = this.number.get(i) - numberToSubtract.number.get(i) - carry;
            if (diff < 0) {
                diff += maxInteger;
                carry = 1;
            } else {
                carry = 0;
            }
            resultArray.add(diff);
        }

        //Add digits of first number remaining
        for (int i = length2; i < length1; i++) {
            int diff = this.number.get(i) - carry;

            if (diff < 0) {
                diff += maxInteger;
                carry = 1;
            } else {
                carry = 0;
            }
            resultArray.add(diff);
        }

        //Reverse array list
        Collections.reverse(resultArray);
        Collections.reverse(this.number);
        Collections.reverse(numberToSubtract.number);

        int index = 0;
        while (index < resultArray.size() && resultArray.get(index) == 0) {
            resultArray.remove(index);
        }
        //Check if result array is full of 0
        if (index == resultArray.size()) {
            return new BigInt("0");
        }

        return new BigInt(resultArray);
    }

    public BigInt multiplyNumber(BigInt numberToMultiply) {

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
        for (int i = length1 - 1; i >= 0; i--) {

            //Reset carry to 0
            long carry = 0;

            //reset position of index2 for each loop on first number
            index2 = 0;

            //Go through second number from back
            for (int j = length2 - 1; j >= 0; j--) {


                //Multiply two digit, add result stored at current indexes and carry
                long product = (long) this.number.get(i) * numberToMultiply.number.get(j) + interResult[index1 + index2] + carry;
                //Get new carry
                carry = product / maxInteger;

                //Store result
                interResult[index1 + index2] = product % maxInteger;
                //Increase index2 because next digit of number2
                index2++;
            }
            //After last digit of number2, store carry in the result array
            if (carry > 0) {
                interResult[index1 + index2] += carry;
            }

            //Increase index1 because next digit of number1
            index1++;
        }

        //Remove 0 from the right in result array
        int index = interResult.length - 1;
        while (index >= 0 && interResult[index] == 0) {
            index--;
        }

        //Check if result array is full of 0
        if (index == -1) {
            return new BigInt("0");
        }

        //Get each result digit and store in final result
        while (index >= 0) {
            resultArray.add((int) interResult[index--]);
        }


        return new BigInt(resultArray);
    }

    public BigInt addModulusNumber(BigInt numberToAdd, BigInt modulus) {
        BigInt newNumber = new BigInt(this.number);
        while (newNumber.isSmaller(modulus) == false) {
            newNumber = newNumber.subtractNumber(modulus);
        }

        BigInt newNumberToAdd = new BigInt(numberToAdd.number);
        while (newNumberToAdd.isSmaller(modulus) == false) {
            newNumberToAdd = newNumberToAdd.subtractNumber(modulus);
        }
        BigInt sum = newNumber.addNumber(newNumberToAdd);

        while (sum.isSmaller(modulus) == false) {
            sum = sum.subtractNumber(modulus);
        }
        return sum;
    }

    public BigInt subtractModulusNumber(BigInt numberToSubtract, BigInt modulus) {
        BigInt diff = this.subtractNumber(numberToSubtract);

        while (diff.isSmaller(modulus) == false) {
            diff = diff.subtractNumber(modulus);
        }
        return diff;


    }

    public boolean isSmaller(BigInt numberToCompare) {

        if (this.number.size() < numberToCompare.number.size()) {
            return true;
        }
        if (this.number.size() > numberToCompare.number.size()) {
            return false;
        }

        for (int i = 0; i < this.number.size(); i++) {
            if (this.number.get(i) < numberToCompare.number.get(i)) {
                return true;
            } else if (this.number.get(i) > numberToCompare.number.get(i)) {
                return false;
            }
        }
        return false;
    }

    public BigInt modulus10(BigInt modulus) {
        //modulus = 10^k, get k aka number of digits in modulus
        int k = (modulus.number.size() - 1) * 9 + String.valueOf(modulus.number.get(0)).length() - 1;
        //Store length of number
        int length = this.number.size();

        //Number of full int to keep
        int fullKeep = k / 9;

        //Number of digit to keep in last int before full int keep
        int nbDigits = k % 9;

        //New arraylist for result
        ArrayList<Integer> resultArray = new ArrayList<Integer>();
        for (int i = length - 1; i >= length - fullKeep; i--) {
            resultArray.add(this.number.get(i));
        }

        if (nbDigits != 0) {
            String temp = Integer.toString(this.number.get(length - 1 - fullKeep));
            String str = temp.substring(temp.length() - nbDigits);
            resultArray.add(Integer.parseInt(str));
        }
        Collections.reverse(resultArray);
        return new BigInt(resultArray);

    }

    public BigInt divBy10(BigInt divisor) {
        //Divisor = 10^k, get k
        int k = (divisor.number.size() - 1) * 9 + String.valueOf(divisor.number.get(0)).length() - 1;
        //Store length of number
        int length = this.number.size();

        //Number of full int to drop
        int fullDrop = k / 9;

        //Number of digit to drop in last int before full int drop
        int nbDigits = k % 9;

        StringBuilder sb = new StringBuilder();
        //ArrayList<Integer> resultArray = new ArrayList<Integer>();
        if(nbDigits != 0){
            for (int i = 0; i < length - 1 - fullDrop; i++) {
                String temp = Integer.toString(this.number.get(i));
                while (temp.length() < 9) {
                    temp = '0' + temp;
                }
                sb.append(temp);
            }
            String temp = Integer.toString(this.number.get(length - 1 - fullDrop));
            while (temp.length() < 9) {
                temp = '0' + temp;
            }
            String str = temp.substring(0, temp.length() - nbDigits);
            sb.append(str);
        }
        else{
            for (int i = 0; i < length - fullDrop; i++) {
                String temp = Integer.toString(this.number.get(i));
                while (temp.length() < 9) {
                    temp = '0' + temp;
                }
                sb.append(temp);
            }
        }

        return new BigInt(sb.toString());
    }

    public BigInt montgomeryMultiply(BigInt numberToMultiply, BigInt modulus, BigInt auxiliaryModulus, BigInt invOppositeModulus) {
        BigInt S = this.multiplyNumber(numberToMultiply);

        BigInt T = (S.multiplyNumber(invOppositeModulus)).modulus10(auxiliaryModulus);

        BigInt M = (S.addNumber(T.multiplyNumber(modulus)));

        BigInt U = M.divBy10(auxiliaryModulus);

        if (!U.isSmaller(modulus)) {
            return U.subtractNumber(modulus);
        } else {
            return U;
        }
    }

    public BigInt modMultiply(BigInt numberToMultiply, BigInt modulus, BigInt auxiliaryModulus, BigInt invOppositeModulus, BigInt r_2modn) {
        //Transform this into montgomery representation
        BigInt newThis = this.montgomeryMultiply(r_2modn, modulus, auxiliaryModulus, invOppositeModulus);

        //Transform numberToMultiply into montgomery representation
        BigInt newNumberToMultiply = numberToMultiply.montgomeryMultiply(r_2modn, modulus, auxiliaryModulus, invOppositeModulus);

        //Calculate montgomery representation of product this by numberToMultiply
        BigInt newProduct = newThis.montgomeryMultiply(newNumberToMultiply, modulus, auxiliaryModulus, invOppositeModulus);

        //Transform montgomery representation back to normal representation
        BigInt product = newProduct.montgomeryMultiply(new BigInt("1"), modulus, auxiliaryModulus, invOppositeModulus);

        return product;
    }

    public String toString() {

        String res = "";
        res += Integer.toString(this.number.get(0));
        for (int i = 1; i < this.number.size(); i++) {
            String temp = Integer.toString(this.number.get(i));
            while (temp.length() < 9) {
                temp = '0' + temp;
            }
            res += temp;
        }
        return res;
    }

    public BigInt modularExpo(ArrayList<Character> k, BigInt modulus, BigInt auxiliaryModulus, BigInt invOppositeModulus, BigInt r_2modn){
        BigInt newThis = this.montgomeryMultiply(r_2modn, modulus, auxiliaryModulus, invOppositeModulus);
        BigInt P = auxiliaryModulus.subtractNumber(modulus);
        int i = k.size()-1;

        while(i>= 0){
            P = P.montgomeryMultiply(P, modulus, auxiliaryModulus, invOppositeModulus);
            if(k.get(i) == '1'){
                P = P.montgomeryMultiply(newThis, modulus, auxiliaryModulus, invOppositeModulus);
            }
            i--;
        }

        BigInt newP = P.montgomeryMultiply(new BigInt("1"), modulus, auxiliaryModulus, invOppositeModulus);
        return newP;
    }

    public static void main(String[] args) {
        String num1 = "40439259280043854394384724185215749617014043121956053583069354910110521930087897686954214880699127781861079392681714927097809127646849818195762273579332978298089179502487838235347910285752692409785787478420483372749541044551245805611185128588941837082731512683451837605726025272724743514315790484732647013879";
        String num2 = "14761283389341084194002215078975668519400256859157264982040334961059740939613021614698223379747222083756600079268157839569913528455569682736184083845992508723353976741692130256045632959847424355650571301381751510405049722072263914344963486309696781267160573814092959220315966655838299897980514271971826055673";
        String mod = "156774238246875915835445233811147609873343164059043855105640486188519457028233998002402904757751807605965928975478596347242014672224727067633004297628621362248433746107510884059233724941256971566153218713448788958681155977990828154489255005329847271334645254216377998443414892683976459272966032042624948290751";
        String r = "1000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
        String r2modn = "92798925215233083812673353035055163940314047691642629923513530301947487382655125673044188687546889317664932548994412820492643745381141618263739211910740182544323134862426706103941302404142957103082814349023973051723872383480128214438229607259635988317976363986289766043178811423125396681631497769849035074342";
        String v = "147023683612895283756207020407708841813901693073581443767279168651495753676708816823421540743275385867627569778881477607143849212681550052866268668936159852722149471612780495424587630129672095835032476977598301540446435595609594990402834732036243059778302592270889714616348891031730521372416880983684615853249";


        /*String num1 = "987654321";
        String num2 = "123456789";
        String mod = "189583723";
        String r = "1000000000";
        String v = "419070787";
        String r2modn = "30025941";*/


        BigInt bigint1 = new BigInt(num1);
        BigInt bigint2 = new BigInt(num2);
        BigInt modulus = new BigInt(mod);
        BigInt auxiliaryModulus = new BigInt(r);
        BigInt r_2modn = new BigInt(r2modn);
        BigInt invOppositeModulus = new BigInt(v);

        //Tests unitaires
        //Addition
        /*System.out.println("Test of add function");
        long addStartTime = System.nanoTime();
        BigInt addResult = bigint1.addNumber(bigint2);
        long addEndTime = System.nanoTime();
        long addOutput = addEndTime - addStartTime;
        System.out.println("Time Execution: " + addOutput + " ns");
        BigInt expectedAddResult = new BigInt("55200542669384938588386939264191418136414299981113318565109689871170262869700919301652438260446349865617679471949872766667722656102419500931946357425325487021443156244179968491393543245600116765436358779802234883154590766623509719956148614898638618349892086497544796826041991928563043412296304756704473069552");
        if(addResult.number.equals(expectedAddResult.number)== true){
            System.out.println("Success: result equals the expected result");
        }
        else{
            System.out.println("Failure: result is not equal to expected result");
        }*/

        //Soustraction
        /*System.out.println();
        System.out.println("Test of subtract function");
        long subStartTime = System.nanoTime();
        BigInt subResult = bigint1.subtractNumber(bigint2);
        long subEndTime = System.nanoTime();
        long subOutput = subEndTime - subStartTime;
        System.out.println("Time Execution: " + subOutput + " ns");
        BigInt expectedSubResult = new BigInt("25677975890702770200382509106240081097613786262798788601029019949050780990474876072255991500951905698104479313413557087527895599191280135459578189733340469574735202760795707979302277325905268054135216177038731862344491322478981891266221642279245055815570938869358878385410058616886443616335276212760820958206");
        if(subResult.number.equals(expectedSubResult.number)){
            System.out.println("Success: result equals the expected result");
        }
        else{
            System.out.println("Failure: result is not equal to expected result");
        }*/

        //Multiplication
        /*System.out.println();
        System.out.println("Test of multiply function");
        long multStartTime = System.nanoTime();
        BigInt multResult = bigint1.multiplyNumber(bigint2);
        long multEndTime = System.nanoTime();
        long multOutput = multEndTime - multStartTime;
        System.out.println("Time Execution: " + multOutput + " ns");
        BigInt expectedMultResult = new BigInt("596935366287768639220946238504356709185746816694260997549482635475124124333632394244105383844437314966439056681556273724428345393357430111583832669049202356493648372719672534603184924421011056889341948237994744203132919242613628846456087221313975498029820161876663805738744987137564525374587313572664983769009644621276140019562881792400201382050163189286945671732825972129747905442526044709224819238314855961153038483034965031034928377441299097857017054731934616391830891088162294628329920765906000552275793232733213999849965081499421476193176601227377629075895324845402580353371238891594363295687640639630257685567");
        if(multResult.number.equals(expectedMultResult.number)){
            System.out.println("Success: result equals the expected result");
        }
        else{
            System.out.println("Failure: result is not equal to expected result");
            }*/

        //Addition modulaire
        /*System.out.println();
        System.out.println("Test of add with modulus function");
        long addModStartTime = System.nanoTime();
        BigInt addModulusResult = bigint1.addModulusNumber(bigint2, modulus);
        long addModEndTIme = System.nanoTime();
        long addModOutput = addModEndTIme - addModStartTime;
        System.out.println("Time Execution: " + addModOutput + " ns");
        BigInt expectedAddModulusResult = new BigInt("55200542669384938588386939264191418136414299981113318565109689871170262869700919301652438260446349865617679471949872766667722656102419500931946357425325487021443156244179968491393543245600116765436358779802234883154590766623509719956148614898638618349892086497544796826041991928563043412296304756704473069552");
        if(addModulusResult.number.equals(expectedAddModulusResult.number)){
            System.out.println("Success: result equals the expected result");
        }
        else{
            System.out.println("Failure: result is not equal to expected result");
        }*/

        //Soustraction modulaire
        /*System.out.println();
        System.out.println("Test of subtract modulus function");
        long subModStartTime = System.nanoTime();
        BigInt subModulusResult = bigint1.subtractModulusNumber(bigint2,modulus);
        long subModEndTime = System.nanoTime();
        long subModOutput = subModEndTime - subModStartTime;
        System.out.println("Time Execution: " + subModOutput + " ns");
        BigInt expectedSubModulusResult = new BigInt("25677975890702770200382509106240081097613786262798788601029019949050780990474876072255991500951905698104479313413557087527895599191280135459578189733340469574735202760795707979302277325905268054135216177038731862344491322478981891266221642279245055815570938869358878385410058616886443616335276212760820958206");
        if(subModulusResult.number.equals((expectedSubModulusResult.number))){
            System.out.println("Success: result equals the expected result");
        }
        else{
            System.out.println("Failure: result is not equal to expected result");
            System.out.println(subModulusResult.number);
            System.out.println(expectedSubModulusResult.number);
        }*/

        //Multiplication de Montgomery
       /*System.out.println();
        BigInteger bi1 = new BigInteger("40439259280043854394384724185215749617014043121956053583069354910110521930087897686954214880699127781861079392681714927097809127646849818195762273579332978298089179502487838235347910285752692409785787478420483372749541044551245805611185128588941837082731512683451837605726025272724743514315790484732647013879");
        BigInteger bi2 = new BigInteger("14761283389341084194002215078975668519400256859157264982040334961059740939613021614698223379747222083756600079268157839569913528455569682736184083845992508723353976741692130256045632959847424355650571301381751510405049722072263914344963486309696781267160573814092959220315966655838299897980514271971826055673");
        BigInteger Ni = new BigInteger("156774238246875915835445233811147609873343164059043855105640486188519457028233998002402904757751807605965928975478596347242014672224727067633004297628621362248433746107510884059233724941256971566153218713448788958681155977990828154489255005329847271334645254216377998443414892683976459272966032042624948290751");
        BigInteger bi3 = (bi1.multiply(bi2)).mod(Ni);
        System.out.println("Test of montgomery multiplication function");
        long montgomeryStartTime = System.nanoTime();
        BigInt montgomeryResult = bigint1.modMultiply(bigint2, modulus, auxiliaryModulus, invOppositeModulus, r_2modn);
        long montgomeryEndTime = System.nanoTime();
        long montgomeryOutput = montgomeryEndTime - montgomeryStartTime;
        System.out.println("Time Execution: " + montgomeryOutput + " ns");
        BigInteger res = new BigInteger(montgomeryResult.toString());
        if(res.equals(bi3)){
            System.out.println("Success: result equals the expected result");
            System.out.println("Result: " + montgomeryResult.toString());
        }
        else{
            System.out.println("Failure");
        }*/

        //Exponentiation modulaire
        System.out.println();
        BigInteger bi1 = new BigInteger("40439259280043854394384724185215749617014043121956053583069354910110521930087897686954214880699127781861079392681714927097809127646849818195762273579332978298089179502487838235347910285752692409785787478420483372749541044551245805611185128588941837082731512683451837605726025272724743514315790484732647013879");
        BigInteger bi2 = new BigInteger("14761283389341084194002215078975668519400256859157264982040334961059740939613021614698223379747222083756600079268157839569913528455569682736184083845992508723353976741692130256045632959847424355650571301381751510405049722072263914344963486309696781267160573814092959220315966655838299897980514271971826055673");
        BigInteger Ni = new BigInteger("156774238246875915835445233811147609873343164059043855105640486188519457028233998002402904757751807605965928975478596347242014672224727067633004297628621362248433746107510884059233724941256971566153218713448788958681155977990828154489255005329847271334645254216377998443414892683976459272966032042624948290751");
        BigInteger bi3 = bi1.modPow(bi2, Ni);

        String exp = bi2.toString(2);
        ArrayList<Character> exponent = new ArrayList<Character>();
        for(int i=0; i<exp.length(); i++){
            exponent.add(exp.charAt(i));
        }

        System.out.println("Test of modular exponentiation function");
        long exponentiationStartTime = System.nanoTime();
        BigInt exponentiationResult = bigint1.modularExpo(exponent, modulus, auxiliaryModulus, invOppositeModulus, r_2modn);
        long exponentiationEndTime = System.nanoTime();
        long exponentiationOutput = exponentiationEndTime - exponentiationStartTime;
        System.out.println("Time Execution: " + exponentiationOutput + " ns");
        BigInteger res = new BigInteger(exponentiationResult.toString());
        if(res.equals(bi3)){
            System.out.println("Success: result equals the expected result");
        }
        else{
            System.out.println("Failure");
            System.out.println(exponentiationResult.toString());
            System.out.println(bi3.toString());
        }
        //Recuperation temps d'execution

        //Addition
        /*System.out.println("Récupération des temps d'executions pour 100 additions de grand nomnbres");
        ArrayList<Long> executionTimes = new ArrayList<Long>();
        for (int i = 0; i < 100; i++) {

            //Store values in BigInt
            BigInt firstNumber = new BigInt();
            firstNumber = firstNumber.randomBigNumber();
            BigInt secondNumber = new BigInt();
            secondNumber = secondNumber.randomBigNumber();

            BigInteger number1 = new BigInteger(firstNumber.toString());
            BigInteger number2 = new BigInteger(secondNumber.toString());
            BigInteger expectedResult = number1.add(number2);
            System.out.println("Addition n° " + (i+1));
            long startTime = System.nanoTime();
            BigInt result = firstNumber.addNumber(secondNumber);
            long endTime = System.nanoTime();
            long timeOutput = endTime - startTime;
            System.out.println("Execution time: " + timeOutput + " ns");

            BigInteger resulti = new BigInteger(result.toString());
            if(resulti.equals(expectedResult)){
                System.out.println("Success: result equals the expected result");
            }
            else{
                System.out.println("Failure");
            }
            executionTimes.add(timeOutput);
        }
        long sum = 0;
        long avgExecutionTime;
        for(int i= 0; i< executionTimes.size(); i++)
            sum+=executionTimes.get(i);
        avgExecutionTime = sum / executionTimes.size();

        System.out.println("The average execution time is: " + avgExecutionTime + " ns");*/

        //Soustraction
        /*System.out.println("Récupération des temps d'executions pour 100 soustractions de grand nomnbres");
        ArrayList<Long> executionTimes = new ArrayList<Long>();
        for (int i = 0; i < 100; i++) {

            //Store values in BigInt
            BigInt firstNumber = new BigInt();
            firstNumber = firstNumber.randomBigNumber();
            BigInt secondNumber = new BigInt();
            secondNumber = secondNumber.randomBigNumber();

            BigInteger number1 = new BigInteger(firstNumber.toString());
            BigInteger number2 = new BigInteger(secondNumber.toString());
            if(number1.compareTo(number2) <0){
                BigInteger temp= number1;
                number1 = number2;
                number2 = temp;
            }
            BigInteger expectedResult = number1.subtract(number2);
            System.out.println("Soustraction n° " + (i+1));
            long startTime = System.nanoTime();
            BigInt result = firstNumber.subtractNumber(secondNumber);
            long endTime = System.nanoTime();
            long timeOutput = endTime - startTime;
            System.out.println("Execution time: " + timeOutput + " ns");

            BigInteger resulti = new BigInteger(result.toString());
            if(resulti.equals(expectedResult)){
                System.out.println("Success: result equals the expected result");
            }
            else{
                System.out.println("Failure");
            }
            executionTimes.add(timeOutput);
        }
        long sum = 0;
        long avgExecutionTime;
        for(int i= 0; i< executionTimes.size(); i++)
            sum+=executionTimes.get(i);
        avgExecutionTime = sum / executionTimes.size();

        System.out.println("The average execution time is: " + avgExecutionTime + " ns");*/

        //Multiplication
        /*System.out.println("Récupération des temps d'executions pour 100 multiplications de grand nomnbres");
        ArrayList<Long> executionTimes = new ArrayList<Long>();
        for (int i = 0; i < 100; i++) {

            //Store values in BigInt
            BigInt firstNumber = new BigInt();
            firstNumber = firstNumber.randomBigNumber();
            BigInt secondNumber = new BigInt();
            secondNumber = secondNumber.randomBigNumber();

            BigInteger number1 = new BigInteger(firstNumber.toString());
            BigInteger number2 = new BigInteger(secondNumber.toString());
            BigInteger expectedResult = number1.multiply(number2);
            System.out.println("Multiplication n° " + (i+1));
            long startTime = System.nanoTime();
            BigInt result = firstNumber.multiplyNumber(secondNumber);
            long endTime = System.nanoTime();
            long timeOutput = endTime - startTime;
            System.out.println("Execution time: " + timeOutput + " ns");

            BigInteger resulti = new BigInteger(result.toString());
            if(resulti.equals(expectedResult)){
                System.out.println("Success: result equals the expected result");
            }
            else{
                System.out.println("Failure");
            }
            executionTimes.add(timeOutput);
        }
        long sum = 0;
        long avgExecutionTime;
        for(int i= 0; i< executionTimes.size(); i++)
            sum+=executionTimes.get(i);
        avgExecutionTime = sum / executionTimes.size();

        System.out.println("The average execution time is: " + avgExecutionTime + " ns");*/

        //Addition modulaire
        /*System.out.println("Récupération des temps d'executions pour 100 additions modulaire de grand nomnbres");
        ArrayList<Long> executionTimes = new ArrayList<Long>();
        for (int i = 0; i < 100; i++) {
            BigInteger Ni = new BigInteger("156774238246875915835445233811147609873343164059043855105640486188519457028233998002402904757751807605965928975478596347242014672224727067633004297628621362248433746107510884059233724941256971566153218713448788958681155977990828154489255005329847271334645254216377998443414892683976459272966032042624948290751");


            //Store values in BigInt
            BigInt firstNumber = new BigInt();
            firstNumber = firstNumber.randomBigNumber();
            BigInt secondNumber = new BigInt();
            secondNumber = secondNumber.randomBigNumber();

            BigInteger number1 = new BigInteger(firstNumber.toString());
            BigInteger number2 = new BigInteger(secondNumber.toString());
            BigInteger expectedResult = (number1.add(number2)).mod(Ni);
            System.out.println("Addition modulaire n° " + (i+1));
            long startTime = System.nanoTime();
            BigInt result = firstNumber.addModulusNumber(secondNumber, modulus);
            long endTime = System.nanoTime();
            long timeOutput = endTime - startTime;
            System.out.println("Execution time: " + timeOutput + " ns");

            BigInteger resulti = new BigInteger(result.toString());
            if(resulti.equals(expectedResult)){
                System.out.println("Success: result equals the expected result");
            }
            else{
                System.out.println("Failure");
            }
            executionTimes.add(timeOutput);
        }
        long sum = 0;
        long avgExecutionTime;
        for(int i= 0; i< executionTimes.size(); i++)
            sum+=executionTimes.get(i);
        avgExecutionTime = sum / executionTimes.size();

        System.out.println("The average execution time is: " + avgExecutionTime + " ns");*/

        //Soustraction Modulaire
         /*System.out.println("Récupération des temps d'executions pour 100 soustractions modulaire de grand nomnbres");
        ArrayList<Long> executionTimes = new ArrayList<Long>();
        for (int i = 0; i < 100; i++) {

            BigInteger Ni = new BigInteger("156774238246875915835445233811147609873343164059043855105640486188519457028233998002402904757751807605965928975478596347242014672224727067633004297628621362248433746107510884059233724941256971566153218713448788958681155977990828154489255005329847271334645254216377998443414892683976459272966032042624948290751");

            //Store values in BigInt
            BigInt firstNumber = new BigInt();
            firstNumber = firstNumber.randomBigNumber();
            BigInt secondNumber = new BigInt();
            secondNumber = secondNumber.randomBigNumber();

            BigInteger number1 = new BigInteger(firstNumber.toString());
            BigInteger number2 = new BigInteger(secondNumber.toString());
            if(number1.compareTo(number2) <0){
                BigInteger temp= number1;
                number1 = number2;
                number2 = temp;
            }
            BigInteger expectedResult = (number1.subtract(number2)).mod(Ni);
            System.out.println("Soustraction modulaire n° " + (i+1));
            long startTime = System.nanoTime();
            BigInt result = firstNumber.subtractModulusNumber(secondNumber, modulus);
            long endTime = System.nanoTime();
            long timeOutput = endTime - startTime;
            System.out.println("Execution time: " + timeOutput + " ns");

            BigInteger resulti = new BigInteger(result.toString());
            if(resulti.equals(expectedResult)){
                System.out.println("Success: result equals the expected result");
            }
            else{
                System.out.println("Failure");
            }
            executionTimes.add(timeOutput);
        }
        long sum = 0;
        long avgExecutionTime;
        for(int i= 0; i< executionTimes.size(); i++)
            sum+=executionTimes.get(i);
        avgExecutionTime = sum / executionTimes.size();

        System.out.println("The average execution time is: " + avgExecutionTime + " ns");*/

        //Multiplication de Montgomery
        /*System.out.println("Récupération des temps d'executions pour 100 multiplication de montgomery");
        ArrayList<Long> executionTimes = new ArrayList<Long>();
        for (int i = 0; i < 100; i++) {


            BigInteger Ni = new BigInteger("156774238246875915835445233811147609873343164059043855105640486188519457028233998002402904757751807605965928975478596347242014672224727067633004297628621362248433746107510884059233724941256971566153218713448788958681155977990828154489255005329847271334645254216377998443414892683976459272966032042624948290751");


            //Store values in BigInt
            BigInt firstNumber = new BigInt();
            firstNumber = firstNumber.randomBigNumber();
            BigInt secondNumber = new BigInt();
            secondNumber = secondNumber.randomBigNumber();

            BigInteger number1 = new BigInteger(firstNumber.toString());
            BigInteger number2 = new BigInteger(secondNumber.toString());
            BigInteger expectedResult = (number1.multiply(number2)).mod(Ni);
            System.out.println("Montgomery multplication n° " + (i+1));
            long startTime = System.nanoTime();
            BigInt result = firstNumber.modMultiply(secondNumber, modulus, auxiliaryModulus, invOppositeModulus, r_2modn);
            long endTime = System.nanoTime();
            long timeOutput = endTime - startTime;
            System.out.println("Execution time: " + timeOutput + " ns");

            BigInteger resulti = new BigInteger(result.toString());
            if(resulti.equals(expectedResult)){
                System.out.println("Success: result equals the expected result");
            }
            else{
                System.out.println("Failure");
            }
            executionTimes.add(timeOutput);
        }
        long sum = 0;
        long avgExecutionTime;
        for(int i= 0; i< executionTimes.size(); i++)
            sum+=executionTimes.get(i);
        avgExecutionTime = sum / executionTimes.size();

        System.out.println("The average execution time is: " + avgExecutionTime + " ns");*/
    }
}


