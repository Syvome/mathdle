package com.example.mathdle;

import android.content.ClipData;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.DragEvent;
import android.view.View;

import android.widget.Button;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;


import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity
        implements TextToSpeech.OnInitListener  {

    Random random = new Random();
    TextToSpeech tts;
    SharedPreferences prefs;
    TextView plus, minus, multiply, divide;
    TextView resultText;
    int score = 0;
    TextView scoreText;
    Button nextButton;


    int[] numbers = new int[4];
    int target;
    private String expr;
    private int pos;
    View draggedNumberView;
    TextView n1,n2,n3,n4,op1, op2, op3,p1,p2,p3,p4,p5,p6;

    TextView parenthesisOpen, parenthesisClose;
    TextView targetText;
    boolean puzzleSolved = false;
    @Override
    public void onInit(int status) {

        if(status == TextToSpeech.SUCCESS){

            tts.setLanguage(Locale.ENGLISH);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        tts = new TextToSpeech(this, this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefs = getSharedPreferences(
                "mathdle",
                MODE_PRIVATE
        );
        int highscore = prefs.getInt("highscore", 0);

        resultText = findViewById(R.id.resultText);
        plus = findViewById(R.id.plus);

        scoreText = findViewById(R.id.scoreText);
        op1 = findViewById(R.id.op1);
        op2 = findViewById(R.id.op2);
        op3 = findViewById(R.id.op3);
        minus = findViewById(R.id.minus);
        multiply = findViewById(R.id.multiply);
        divide = findViewById(R.id.divide);
        n1 = findViewById(R.id.n1);
        n2 = findViewById(R.id.n2);
        n3 = findViewById(R.id.n3);
        n4 = findViewById(R.id.n4);
        TextView[] numberViews = {n1, n2, n3, n4};
        parenthesisOpen = findViewById(R.id.openparenthesis);
        parenthesisClose = findViewById(R.id.closeparenthesis);
        p1 = findViewById(R.id.p1);
        p2 = findViewById(R.id.p2);
        p3 = findViewById(R.id.p3);
        p4 = findViewById(R.id.p4);
        p5 = findViewById(R.id.p5);
        p6 = findViewById(R.id.p6);
        p1.setVisibility(View.INVISIBLE);
        p2.setVisibility(View.INVISIBLE);
        p3.setVisibility(View.INVISIBLE);
        p4.setVisibility(View.INVISIBLE);
        p5.setVisibility(View.INVISIBLE);
        p6.setVisibility(View.INVISIBLE);

        targetText = findViewById(R.id.target);
        nextButton = findViewById(R.id.nextButton);

        for (TextView tv : numberViews) {
            tv.setOnLongClickListener(v -> {
                ClipData data = ClipData.newPlainText("", ((TextView)v).getText());
                View.DragShadowBuilder shadow = new View.DragShadowBuilder(v);
                v.startDragAndDrop(data, shadow, v, 0);
                draggedNumberView = v; // mémorise la vue d'origine
                return true;
            });

            tv.setOnDragListener((v, event) -> {
                if(event.getAction() == DragEvent.ACTION_DROP){
                    // Intervertir les nombres
                    String sourceText = ((TextView)draggedNumberView).getText().toString();
                    String targetText = ((TextView)v).getText().toString();

                    ((TextView)v).setText(sourceText);
                    ((TextView)draggedNumberView).setText(targetText);

                    // Mettre à jour le tableau numbers[]
                    updateNumbersFromViews();

                    updateResult(); // recalculer le résultat
                }
                return true;
            });
        }
        View.OnLongClickListener dragListener = v -> {

            String text = ((TextView)v).getText().toString();

            // si c'est une parenthèse → afficher les zones
            if(text.equals("(") || text.equals(")")){
                showParenthesisSlots();
            }

            ClipData data = ClipData.newPlainText("", text);
            View.DragShadowBuilder shadow = new View.DragShadowBuilder(v);

            v.startDragAndDrop(data, shadow, v, 0);
            return true;

        };

        plus.setOnLongClickListener(dragListener);
        minus.setOnLongClickListener(dragListener);
        multiply.setOnLongClickListener(dragListener);
        divide.setOnLongClickListener(dragListener);
        findViewById(R.id.op1).setOnDragListener(dropListener);
        findViewById(R.id.op2).setOnDragListener(dropListener);
        findViewById(R.id.op3).setOnDragListener(dropListener);
        parenthesisOpen.setOnLongClickListener(dragListener);
        parenthesisClose.setOnLongClickListener(dragListener);
        findViewById(R.id.p1).setOnDragListener(dropListener);
        findViewById(R.id.p2).setOnDragListener(dropListener);
        findViewById(R.id.p3).setOnDragListener(dropListener);
        findViewById(R.id.p4).setOnDragListener(dropListener);
        findViewById(R.id.p5).setOnDragListener(dropListener);
        findViewById(R.id.p6).setOnDragListener(dropListener);
        n1.setText("7");
        n2.setText("5");
        n3.setText("3");
        n4.setText("2");
        generatePuzzle();
        nextButton.setEnabled(false);
        nextButton.setOnClickListener(v -> {

            if(!puzzleSolved) return;

            score++;
            if(prefs.getBoolean("sound", true)){
                tts.speak("Correct answer",TextToSpeech.QUEUE_FLUSH,null,null);
            }
            if(score > prefs.getInt("highscore",0)){
                prefs.edit().putInt("highscore", score).apply();
            }
            scoreText.setText("Score : " + score);
            generatePuzzle();
            resetOperatorsAndParentheses();
            resultText.setText("Result : 0");
            nextButton.setEnabled(false);
            puzzleSolved = false;
        });

    }
    @Override
    protected void onDestroy() {

        if(tts != null){

            tts.stop();
            tts.shutdown();
        }

        super.onDestroy();
    }

    void updateNumbersFromViews() {
        numbers[0] = Integer.parseInt(n1.getText().toString());
        numbers[1] = Integer.parseInt(n2.getText().toString());
        numbers[2] = Integer.parseInt(n3.getText().toString());
        numbers[3] = Integer.parseInt(n4.getText().toString());
    }

    // Remet les opérateurs et parenthèses à l'état initial
    void resetOperatorsAndParentheses() {
        // Réinitialiser les opérateurs
        op1.setText("_");
        op2.setText("_");
        op3.setText("_");
        if(prefs.getBoolean("sound", true)){

            tts.speak(
                    "Operator reset",
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    null
            );
        }

        // Réinitialiser toutes les zones de parenthèses
        p1.setText("_");
        p2.setText("_");
        p3.setText("_");
        p4.setText("_");
        p5.setText("_");
        p6.setText("_");

        // Cacher toutes les TextViews de parenthèses qui sont vides
        hideParenthesisSlots();
    }
    String buildExpression() {

        StringBuilder a = new StringBuilder();



        String o1 = op1.getText().toString();
        String o2 = op2.getText().toString();
        String o3 = op3.getText().toString();

        // ❌ opérateurs incomplets → on bloque
        if(o1.equals("_") || o2.equals("_") || o3.equals("_"))
            return "";

        // sécurité parenthèses
        if(p1.getText().toString().equals("_") && p2.getText().toString().equals("_")
                && p3.getText().toString().equals("_") && p4.getText().toString().equals("_")
                && p5.getText().toString().equals("_") && p6.getText().toString().equals("_")) {

            // pas de parenthèses → expression simple
            return numbers[0] + o1 + numbers[1] + o2 + numbers[2] + o3 + numbers[3];
        }

        // construction propre
        a.append(p1.getText().toString().equals("_") ? "" : p1.getText());
        a.append(numbers[0]);
        a.append(o1);

        a.append(p2.getText().toString().equals("_") ? "" : p2.getText());
        a.append(numbers[1]);

        a.append(p3.getText().toString().equals("_") ? "" : p3.getText());
        a.append(o2);

        a.append(p4.getText().toString().equals("_") ? "" : p4.getText());
        a.append(numbers[2]);

        a.append(p5.getText().toString().equals("_") ? "" : p5.getText());
        a.append(o3);
        a.append(numbers[3]);

        a.append(p6.getText().toString().equals("_") ? "" : p6.getText());

        String expr = a.toString();

        // ❌ sécurité finale
        if(expr.endsWith("+") || expr.endsWith("-") ||
                expr.endsWith("×") || expr.endsWith("÷"))
            return "";

        return expr;
    }


    // Update résultat
    void updateResult() {


        try {

            String expression = buildExpression();

            // ❌ sécurité absolue
            if (!isExpressionSafe(expression)) {

                resultText.setText("Result : 0");

                nextButton.setEnabled(false);
                puzzleSolved = false;

                return;
            }

            double result = eval(expression);

            resultText.setText("Result : " + result);

            if (prefs.getBoolean("sound", true)) {
                tts.speak(
                        "result is " + result,
                        TextToSpeech.QUEUE_FLUSH,
                        null,
                        null
                );
            }

            boolean solved = Math.abs(result - target) < 0.001;

            puzzleSolved = solved;
            nextButton.setEnabled(solved);

        } catch (Exception e) {

            resultText.setText("Result : 0");

            nextButton.setEnabled(false);
            puzzleSolved = false;
        }
    }
    boolean isExpressionSafe(String expr) {

        if (expr == null || expr.isEmpty())
            return false;

        // ❌ finit par opérateur
        char last = expr.charAt(expr.length() - 1);
        if (last == '+' || last == '-' || last == '×' || last == '÷' || last == '(')
            return false;

        int open = 0;
        boolean lastWasOp = true;

        for (char c : expr.toCharArray()) {

            if (c == '(') open++;

            else if (c == ')') {
                open--;
                if (open < 0) return false;
            }

            else if (c == '+' || c == '-' || c == '×' || c == '÷') {
                if (lastWasOp) return false; // ❌ 2 opérateurs d'affilée
                lastWasOp = true;
                continue;
            }

            else if (Character.isDigit(c)) {
                lastWasOp = false;
            }
        }

        return open == 0 && !lastWasOp;
    }


    // Appliquer opérateur simple
    double applyOp(double a,double b,String op){
        switch(op){
            case "+": return a+b;
            case "-": return a-b;
            case "×": return a*b;
            case "÷": return b!=0?a/b:Double.NaN;
        }
        return Double.NaN;
    }

    // ---------------- Parser pour ordre priorité et parenthèses ----------------
    double eval(String s){
         expr = s.replaceAll("_","");pos=0;
        if(expr.isEmpty()) return 0;
        return parseExpression();
    }

    double parseExpression(){
        double x=parseTerm();
        while(pos<expr.length()){
            char c=expr.charAt(pos);
            if(c=='+'){ pos++; x+=parseTerm(); }
            else if(c=='-'){ pos++; x-=parseTerm(); }
            else break;
        }
        return x;
    }

    double parseTerm(){
        double x=parseFactor();
        while(pos<expr.length()){
            char c=expr.charAt(pos);
            if(c=='×'){ pos++; x*=parseFactor(); }
            else if(c=='÷'){ pos++; x/=parseFactor(); }
            else break;
        }
        return x;
    }

    double parseFactor(){
        char c = expr.charAt(pos);
        if(c=='('){ pos++; double x=parseExpression(); if(pos<expr.length() && expr.charAt(pos)==')') pos++; return x; }
        int start=pos;
        while(pos<expr.length() && Character.isDigit(expr.charAt(pos))) pos++;
        if (start == pos) return 0;
        return Double.parseDouble(expr.substring(start,pos));
    }

    boolean solve(ArrayList<Double> nums, double target){

        if(nums.size()==1){
            return Math.abs(nums.get(0)-target) < 0.0001;
        }

        for(int i=0;i<nums.size();i++){
            for(int j=0;j<nums.size();j++){

                if(i==j) continue;

                ArrayList<Double> remaining = new ArrayList<>();

                for(int k=0;k<nums.size();k++){
                    if(k!=i && k!=j)
                        remaining.add(nums.get(k));
                }

                double a = nums.get(i);
                double b = nums.get(j);

                double[] results = {
                        a+b,
                        a-b,
                        a*b,
                        b!=0 ? a/b : Double.NaN
                };

                for(double r : results){

                    if(Double.isNaN(r)) continue;

                    remaining.add(r);

                    if(solve(remaining,target))
                        return true;

                    remaining.remove(remaining.size()-1);
                }
            }
        }

        return false;
    }


void showParenthesisSlots(){

        int[] ids = {R.id.p1, R.id.p2, R.id.p3, R.id.p4,R.id.p5,R.id.p6};

        for(int id : ids){
            TextView tv = findViewById(id);

            // afficher uniquement si vide
            if(tv.getText().toString().equals("_")){
                tv.setVisibility(View.VISIBLE);
            }
        }
    }

    void hideParenthesisSlots(){

        int[] ids = {R.id.p1, R.id.p2, R.id.p3, R.id.p4,R.id.p5,R.id.p6};

        for(int id : ids){
            TextView tv = findViewById(id);

            // cacher uniquement si vide
            if(tv.getText().toString().equals("_")){
                tv.setVisibility(View.INVISIBLE);
            }
        }
    }

    View.OnDragListener dropListener = (v, event) -> {

        switch(event.getAction()){

            case DragEvent.ACTION_DROP:

                String dragged = event.getClipData().getItemAt(0).getText().toString();
                int id = v.getId();

                // vérifier si c'est un nombre
                boolean isNumber = dragged.matches("\\d+");

                // ---------------- OPERATEURS ----------------

                // interdit parenthèses ET nombres sur opérateurs
                if((id == R.id.op1 || id == R.id.op2 || id == R.id.op3)){

                    if(dragged.equals("(") || dragged.equals(")") || isNumber){
                        return false;
                    }
                }

                // ---------------- PARENTHESES ----------------

                // interdit nombres et opérateurs sur parenthèses
                if(id == R.id.p1 || id == R.id.p2 || id == R.id.p3
                        || id == R.id.p4 || id == R.id.p5 || id == R.id.p6){

                    if(!(dragged.equals("(") || dragged.equals(")"))){
                        return false;
                    }

                    // validation logique
                    if(!canPlaceParenthesis(dragged, id)){
                        return false;
                    }
                }

                // ---------------- NOMBRES ----------------

                // si on drop sur un nombre
                if(id == R.id.n1 || id == R.id.n2 || id == R.id.n3 || id == R.id.n4){

                    // seuls les nombres sont autorisés
                    if(!isNumber){
                        return false;
                    }

                    // échange des nombres
                    TextView target = (TextView) v;

                    String temp = target.getText().toString();

                    target.setText(dragged);

                    ((TextView)event.getLocalState()).setText(temp);

                    updateNumbersFromViews();
                    updateResult();

                    return true;
                }

                // ---------------- CAS NORMAL ----------------

                ((TextView)v).setText(dragged);

                updateResult();

                break;
            case DragEvent.ACTION_DRAG_ENDED:
                hideParenthesisSlots();
                break;
        }

        return true;
    };

    boolean canPlaceParenthesis(String dragged, int viewId){

        // récupérer les parenthèses actuelles
        String p1 = ((TextView)findViewById(R.id.p1)).getText().toString();
        String p2 = ((TextView)findViewById(R.id.p2)).getText().toString();
        String p3 = ((TextView)findViewById(R.id.p3)).getText().toString();
        String p4 = ((TextView)findViewById(R.id.p4)).getText().toString();
        String p5 = ((TextView)findViewById(R.id.p4)).getText().toString();
        String p6 = ((TextView)findViewById(R.id.p4)).getText().toString();

        String[] all = {p1,p2,p3,p4,p5,p6};

        int open = 0;
        int close = 0;

        for(String s : all){
            if(s.equals("(")) open++;
            if(s.equals(")")) close++;
        }

        // ❌ déjà une parenthèse ici
        TextView current = findViewById(viewId);
        if(!current.getText().toString().equals("_"))
            return false;

        // règle pour "("
        if(dragged.equals("(")){
            return open < 2; // limite simple (max 2 parenthèses ouvertes)
        }

        // règle pour ")"
        if(dragged.equals(")")){
            return open > close; // il faut une "(" ouverte
        }

        return true;
    }

    public void generatePuzzle() {

        boolean valid = false;
        String difficulty =
                prefs.getString("difficulty", "medium");
        nextButton.setEnabled(false);

        while (!valid) {

            for (int i = 0; i < 4; i++) {
                if(difficulty.equals("easy")){

                    numbers[i] = random.nextInt(5) + 1;

                }else if(difficulty.equals("medium")){

                    numbers[i] = random.nextInt(9) + 1;

                }else{

                    numbers[i] = random.nextInt(15) + 1;
                }
            }
            target = random.nextInt(40) + 10;

            ArrayList<Double> list = new ArrayList<>();

            for (int n : numbers)
                list.add((double) n);

            valid = solve(list, target);

        }

        n1.setText(String.valueOf(numbers[0]));
        n2.setText(String.valueOf(numbers[1]));
        n3.setText(String.valueOf(numbers[2]));
        n4.setText(String.valueOf(numbers[3]));

        targetText.setText("Target : " + target);
    }}




