package com.abc.lambda.speed.sample.storm.tfidf.functions;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

//import org.apache.lucene.analysis.StopAnalyzer;
//import org.apache.lucene.analysis.StopFilter;
import com.abc.lambda.speed.sample.storm.tfidf.TfidfTopologyFields;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.spell.PlainTextDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.storm.tuple.Values;

import org.apache.storm.trident.operation.BaseFunction;
import org.apache.storm.trident.operation.TridentCollector;
import org.apache.storm.trident.operation.TridentOperationContext;
import org.apache.storm.trident.tuple.TridentTuple;
//import edu.washington.cs.knowitall.morpha.MorphaStemmer;

public class DocumentTokenizer extends BaseFunction {

    Logger LOG = LoggerFactory.getLogger(DocumentTokenizer.class);

    private static final long serialVersionUID = 1L;


    @Override
    public void execute(TridentTuple tuple, TridentCollector collector) {
        String documentContents = tuple.getStringByField(TfidfTopologyFields.DOCUMENT);
        TokenStream ts = null;
//        try {
//            ts = new StopFilter(
//                    Version.LUCENE_30,
//                    new StandardTokenizer(Version.LUCENE_30, new StringReader(documentContents)),
//                    StopAnalyzer.ENGLISH_STOP_WORDS_SET
//            );
//            CharTermAttribute termAtt = ts.getAttribute(CharTermAttribute.class);
//            while(ts.incrementToken()) {
//                String lemma = MorphaStemmer.stemToken(termAtt.toString());
//                lemma = lemma.trim().replaceAll("\n", "").replaceAll("\r", "");
//                collector.emit(new Values(lemma));
//            }
//            ts.close();
//        } catch (IOException e) {
//            LOG.error(e.toString());
//        }
//        finally {
//            if(ts != null){
//                try {
//                    ts.close();
//                } catch (IOException e) {}
//            }
//
//        }
    }

}
