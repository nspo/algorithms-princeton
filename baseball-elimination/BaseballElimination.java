import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class BaseballElimination {
    private final int numberOfTeams;
    private final String[] names;
    private final HashMap<String, Integer> idOfTeam;
    private final int[] wins;
    private final int[] loss;
    private final int[] left;
    private final int[][] gamesLeftAgainst;
    private final HashMap<Integer, ArrayList<String>> eliminatingTeamsSubset;

    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
        if (filename == null) throw new IllegalArgumentException();

        In infile = new In(filename);
        numberOfTeams = infile.readInt();
        names = new String[numberOfTeams];
        idOfTeam = new HashMap<>();
        wins = new int[numberOfTeams];
        loss = new int[numberOfTeams];
        left = new int[numberOfTeams];
        gamesLeftAgainst = new int[numberOfTeams][numberOfTeams];
        eliminatingTeamsSubset = new HashMap<>();
        for (int i = 0; i < numberOfTeams; ++i) {
            names[i] = infile.readString();
            idOfTeam.put(names[i], i);
            wins[i] = infile.readInt();
            loss[i] = infile.readInt();
            left[i] = infile.readInt();
            for (int j = 0; j < numberOfTeams; j++) {
                if (i == j) {
                    // no games against self
                    // gamesLeftAgainst[i][j] = -1;
                    infile.readString();
                }
                else {
                    gamesLeftAgainst[i][j] = infile.readInt();
                }
            }
        }
    }

    // number of teams
    public int numberOfTeams() {
        return numberOfTeams;
    }

    // all teams
    public Iterable<String> teams() {
        return Arrays.asList(names); // to ensure original order
    }

    // number of wins for given team
    public int wins(String team) {
        if (!idOfTeam.containsKey(team)) throw new IllegalArgumentException();
        return wins[idOfTeam.get(team)];
    }

    // number of losses for given team
    public int losses(String team) {
        if (!idOfTeam.containsKey(team)) throw new IllegalArgumentException();
        return loss[idOfTeam.get(team)];
    }

    // number of remaining games for given team
    public int remaining(String team) {
        if (!idOfTeam.containsKey(team)) throw new IllegalArgumentException();
        return left[idOfTeam.get(team)];
    }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        if (!idOfTeam.containsKey(team1) || !idOfTeam.containsKey(team2))
            throw new IllegalArgumentException();

        return gamesLeftAgainst[idOfTeam.get(team1)][idOfTeam.get(team2)];
    }

    // is given team eliminated?
    public boolean isEliminated(String team) {
        if (!idOfTeam.containsKey(team)) throw new IllegalArgumentException();

        int teamId = idOfTeam.get(team);
        int referenceTeamMaxWins = wins[teamId] + left[teamId];
        // check trivial case
        for (int i = 0; i < numberOfTeams; ++i) {
            if (i == teamId) continue;
            if (referenceTeamMaxWins < wins[i]) {
                eliminatingTeamsSubset.put(teamId, new ArrayList<>());
                eliminatingTeamsSubset.get(teamId).add(names[i]);
                return true;
            }
        }

        // id of each team is also index of vertex of the team (except for teamId, which is invalid)
        int idS = numberOfTeams;
        int idT = numberOfTeams + 1;
        int indexMatchVertexBase = numberOfTeams + 2; // where match vertex indices start
        FlowNetwork fn = new FlowNetwork(
                numberOfTeams + 1 + numberOfTeams * (numberOfTeams - 1) / 2 + 1);

        // connect team vertices to t
        for (int i = 0; i < numberOfTeams; ++i) {
            if (i == teamId) continue;
            fn.addEdge(new FlowEdge(i, idT, Math.max(0, referenceTeamMaxWins - wins[i])));
        }
        // connect each match vertex to s and the participating teams
        int indexMatchVertexOffset = 0;
        for (int i = 0; i < numberOfTeams; ++i) {
            if (i == teamId) continue;
            for (int j = i + 1; j < numberOfTeams; ++j) {
                if (j == teamId) continue;
                int indexMatchVertex = indexMatchVertexBase + indexMatchVertexOffset++;
                // edge from s to match
                fn.addEdge(new FlowEdge(idS, indexMatchVertex, gamesLeftAgainst[i][j]));
                // edge from match to (possibly winning) teams
                fn.addEdge(new FlowEdge(indexMatchVertex, i, Integer.MAX_VALUE));
                fn.addEdge(new FlowEdge(indexMatchVertex, j, Integer.MAX_VALUE));
            }
        }

        FordFulkerson solution = new FordFulkerson(fn, idS, idT);

        int sumMatchesLeft = 0;
        for (int i = 0; i < numberOfTeams; ++i) {
            if (i == teamId) continue;
            for (int j = 0; j < numberOfTeams; ++j) {
                if (j == teamId || j == i) continue;
                sumMatchesLeft += gamesLeftAgainst[i][j];
            }
        }
        sumMatchesLeft /= 2;

        boolean isEliminated = solution.value() != sumMatchesLeft;

        if (isEliminated) {
            // save eliminating subset of teams
            eliminatingTeamsSubset.put(teamId, new ArrayList<>());
            for (int i = 0; i < numberOfTeams; ++i) {
                if (i == teamId) continue;
                if (solution.inCut(i)) eliminatingTeamsSubset.get(teamId).add(names[i]);
            }
        }

        return isEliminated;
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        if (isEliminated(team)) {
            return eliminatingTeamsSubset.get(idOfTeam.get(team));
        }
        else {
            return null;
        }

    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}
