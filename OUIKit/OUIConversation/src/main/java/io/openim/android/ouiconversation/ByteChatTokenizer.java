package io.openim.android.ouiconversation;

import com.linkedin.android.spyglass.tokenization.impl.WordTokenizer;
import com.linkedin.android.spyglass.tokenization.impl.WordTokenizerConfig;

public class ByteChatTokenizer extends WordTokenizer {
    public ByteChatTokenizer(WordTokenizerConfig tokenizerConfig) {
        super(tokenizerConfig);
    }

    @Override
    public boolean isWordBreakingChar(char c) {
        if(c=='@')return false;
        System.out.println("isExplicitChar " + c);
        return true;
    }

}
