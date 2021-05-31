package com.mycompany.twittercito;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

public class TwitList extends JFrame {

    public int cont_positive = 0;
    public int cont_neutral = 0;
    public int cont_negative = 0;

    String tweet = "";

    public static void main(String[] Args) throws MalformedURLException, TwitterException {

        TwitList tl = new TwitList();

        JFrame frame = new JFrame("Tweets Con Polaridad");
        frame.setSize(600, 600);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JLabel label = new JLabel();
        frame.add(label);

        ConfigurationBuilder cb = new ConfigurationBuilder();

        cb.setDebugEnabled(true).setOAuthConsumerKey("koKUA8dwgWL1Qi24u76tRrJ2H")
                .setOAuthConsumerSecret("7ps5ctCCv4Wu5EZzIGaIJDnCxXoSwq07iHhnMACPefRqhAprqN")
                .setOAuthAccessToken("2551820914-mOHqacIhIqRmpDSiayr91IDEt2YAwJaQg3Xp1IX")
                .setOAuthAccessTokenSecret("1YOAADwiRzMwzQMCajD4HnyT3QQnyBeBgn7PvS4ltgBpO");

        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter tw = tf.getInstance();

        // List<Status> status = tw.getHomeTimeline();
        List<Status> consultass = tw.search(new Query("veneco")).getTweets();

        for (int i = 0; i < 12; i++) {

            String[] text = consultass.get(i).getText().split("https:");
            tl.SentimentTweets(text[0].replaceAll("[.;!?(){}\\[\\]<>%]", ""), label);
            tl.POSText(text[0].replaceAll("[.;!?(){}\\[\\]<>%]", ""));

        }

    }

    public void SentimentTweets(String text, JLabel label) {

        StanfordCoreNLP stanfordCoreNLP = Pipeline.getPipeline();
        CoreDocument coreDocument = new CoreDocument(text);

        stanfordCoreNLP.annotate(coreDocument);

        List<CoreSentence> sentences = coreDocument.sentences();

        for (CoreSentence sentence : sentences) {

            String sentiment = sentence.sentiment();
            System.out.println(sentiment + "\t" + sentence);
            tweet = sentiment + " = " + sentence.toString();
            label.setText(tweet);

            if (sentiment.trim().equals("Positive")) {

                cont_positive++;

            } else if (sentiment.trim().equals("Neutral")) {

                cont_neutral++;

            } else if (sentiment.trim().equals("Negative")) {

                cont_negative++;

            }

        }


        // Create dataset
        PieDataset dataset = createDataset(cont_positive, cont_neutral, cont_negative);

            // Create chart
            JFreeChart chart = ChartFactory.createPieChart(
                    "Porcentaje de Polaridad en Tweets",
                    dataset,
                    true,
                    true,
                    false);

            //Format Label
            PieSectionLabelGenerator labelGenerator = new StandardPieSectionLabelGenerator(
                    " {0} : ({2})", new DecimalFormat("0"), new DecimalFormat("0%"));
            ((PiePlot) chart.getPlot()).setLabelGenerator(labelGenerator);

            // Create Panel
            ChartPanel panel = new ChartPanel(chart);
            setContentPane(panel);

            setSize(800, 400);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            setVisible(true);
    }
   
   public void POSText(String text){
        
        StanfordCoreNLP stanfordCoreNLP = Pipeline.getPipeline();
        
        CoreDocument coreDocument = new CoreDocument(text);
        
        stanfordCoreNLP.annotate(coreDocument);
        
        List<CoreLabel> coreLabelList = coreDocument.tokens();
        
        for(CoreLabel coreLabel : coreLabelList){
            
            String pos = coreLabel.get(CoreAnnotations.PartOfSpeechAnnotation.class);
            System.out.println(coreLabel.originalText() + " = "+ pos);
        }
        
    }

    private PieDataset createDataset(int positive, int neutral, int negative) {

        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Positivos", positive);
        dataset.setValue("Neutrales", neutral);
        dataset.setValue("Negativos", negative);

        return dataset;
    }

}
