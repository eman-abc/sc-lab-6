/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class SocialNetworkTest {

    /*
     * TODO: your testing strategies for these methods should go here.
     * See the ic03-testing exercise for examples of what a testing strategy comment looks like.
     * Make sure you have partitions.
     */
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
//    @Test
//    public void testGuessFollowsGraphEmpty() {
//        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(new ArrayList<>());
//        
//        assertTrue("expected empty graph", followsGraph.isEmpty());
//    }
    
    @Test
    public void testGuessFollowsGraphEmptyList() {
        // Test 1: Ensures that an empty list of tweets results in an empty graph.
        // An empty list of tweets should produce an empty follows graph since there is no data to analyze.
        List<Tweet> tweets = new ArrayList<>();
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(tweets);
        assertTrue("expected empty graph for an empty list of tweets", followsGraph.isEmpty());
    }

    @Test
    public void testGuessFollowsGraphNoMentions() {
        // Test 2: Ensures that tweets with no mentions do not add any entries to the follows graph.
        // If no @-mentions are present in the tweets, no relationships should be added to the graph.
        List<Tweet> tweets = List.of(
            new Tweet(1, "alice", "Just saying hi", Instant.now()),
            new Tweet(2, "bob", "What a great day!", Instant.now())
        );
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(tweets);
        assertTrue("expected empty graph with no mentions in tweets", followsGraph.isEmpty());
    }

    @Test
    public void testGuessFollowsGraphSingleMention() {
        // Test 3: Ensures that a single mention in a tweet correctly adds a follower-followed relationship.
        // Alice should follow Bob since Alice mentions Bob in the tweet.
        List<Tweet> tweets = List.of(
            new Tweet(1, "alice", "Hello @bob", Instant.now())
        );
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(tweets);
        assertEquals("alice should follow bob", Set.of("bob"), followsGraph.get("alice"));
    }

    @Test
    public void testGuessFollowsGraphMultipleMentions() {
        // Test 4: Ensures that multiple users mentioned in a single tweet are all followed by the tweet's author.
        // Alice mentions both Bob and Charlie, so she should follow both.
        List<Tweet> tweets = List.of(
            new Tweet(1, "alice", "Hi @bob and @charlie", Instant.now())
        );
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(tweets);
        assertEquals("alice should follow both bob and charlie", Set.of("bob", "charlie"), followsGraph.get("alice"));
    }

    @Test
    public void testGuessFollowsGraphMultipleTweetsFromOneUser() {
        // Test 5: Ensures that multiple tweets from the same user mentioning different people are all captured in the graph.
        // Alice mentions Bob in one tweet and Charlie in another, so she should follow both.
        List<Tweet> tweets = List.of(
            new Tweet(1, "alice", "Hi @bob", Instant.now()),
            new Tweet(2, "alice", "Good morning @charlie", Instant.now())
        );
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(tweets);
        assertEquals("alice should follow both bob and charlie", Set.of("bob", "charlie"), followsGraph.get("alice"));
    }

    @Test
    public void testInfluencersEmptyGraph() {
        // Test 6: Ensures that an empty social network graph results in an empty list of influencers.
        // No users exist in the graph, so the list of influencers should be empty.
        Map<String, Set<String>> followsGraph = new HashMap<>();
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        assertTrue("expected empty list of influencers for an empty follows graph", influencers.isEmpty());
    }

    @Test
    public void testInfluencersSingleUserNoFollowers() {
        // Test 7: Ensures that a user with no followers is not included in the influencers list.
        // Alice has no followers, so the influencers list should be empty.
        Map<String, Set<String>> followsGraph = Map.of("alice", Set.of());
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        assertTrue("expected no influencers when no one has followers", influencers.isEmpty());
    }

    @Test
    public void testInfluencersSingleInfluencer() {
        // Test 8: Ensures that a single user with followers is correctly identified as the top influencer.
        // Bob has one follower (Alice), so he should be listed as the only influencer.
        Map<String, Set<String>> followsGraph = Map.of("alice", Set.of("bob"));
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        assertEquals("bob should be the only influencer", List.of("bob"), influencers);
    }

    public void testInfluencersMultipleInfluencers() {
        // Test 9: Ensures that multiple influencers are ranked in descending order of follower count.
        // Bob has 2 followers, Alice has 1, and Charlie has none, so the list should be in this order: [bob, alice, charlie].
        Map<String, Set<String>> followsGraph = Map.of(
            "bob", Set.of("alice", "charlie"),
            "alice", Set.of("charlie"),
            "charlie", Set.of()
        );
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        assertEquals("expected influencers ordered by follower count", List.of("bob", "alice", "charlie"), influencers);
    }


    @Test
    public void testInfluencersTiedInfluence() {
        // Test 10: Ensures that when multiple users have the same number of followers, they are handled correctly.
        // Alice and Bob both have 1 follower each, so the order between them is arbitrary but both should appear in the list.
        Map<String, Set<String>> followsGraph = Map.of(
            "alice", Set.of("bob"),
            "bob", Set.of("alice")
        );
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        assertTrue("expected both alice and bob to be in the influencers list, in any order", 
                   influencers.containsAll(List.of("alice", "bob")));
    }

    /*
     * Warning: all the tests you write here must be runnable against any
     * SocialNetwork class that follows the spec. It will be run against several
     * staff implementations of SocialNetwork, which will be done by overwriting
     * (temporarily) your version of SocialNetwork with the staff's version.
     * DO NOT strengthen the spec of SocialNetwork or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in SocialNetwork, because that means you're testing a
     * stronger spec than SocialNetwork says. If you need such helper methods,
     * define them in a different class. If you only need them in this test
     * class, then keep them in this test class.
     */

}
