if hadoop jar ./reach_analysis.jar \
  org.apache.giraph.GiraphRunner reach_analysis.ReachAnalysis \
  -vif reach_analysis.ReachVertexInputFormat \
  -vip /new_alias_graphs/CFG/id_stmt_info \
  -vof reach_analysis.ReachVertexOutputFormat \
  -op /reach_res/CFG_W1 \
  -eif reach_analysis.ReachEdgeInputFormat \
  -eip /new_alias_graphs/CFG/final \
  -w 1 \
  -ca giraph.maxCounterWaitMsecs=-1
then
  echo "/new_alias_graphs/CFG------Success"
  echo "/new_alias_graphs/CFG" >> ./alias_result/alias-inline-succ-check.txt
else
  echo "/new_alias_graphs/CFG------Fail"
  echo "/new_alias_graphs/CFG" >> ./alias_result/alias-inline-fail-check.txt
fi
