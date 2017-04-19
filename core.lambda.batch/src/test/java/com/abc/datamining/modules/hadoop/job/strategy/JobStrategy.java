package com.abc.datamining.modules.hadoop.job.strategy;

import com.abc.datamining.modules.hadoop.job.AbstractJob;

public abstract interface JobStrategy {
	
	public abstract int rubJob(AbstractJob job);
}
