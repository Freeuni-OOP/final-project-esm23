package com.quizwebsite.model;

import java.time.LocalDateTime;

public class Quiz {
	private final long id;
	private final long creatorId;
	private String title;
	private String description;
	private boolean randomOrder;
	private boolean onePage;
	private boolean immediateCorrection;
	private boolean practiceEnabled;
	private final LocalDateTime createdAt;

	// For DB reads
	public Quiz(long id, long creatorId, String title, String description, boolean randomOrder, boolean onePage, boolean immediateCorrection, boolean practiceEnabled, LocalDateTime createdAt) {
		this.id = id;
		this.creatorId = creatorId;
		this.title = title;
		this.description = description;
		this.randomOrder = randomOrder;
		this.onePage = onePage;
		this.immediateCorrection = immediateCorrection;
		this.practiceEnabled = practiceEnabled;
		this.createdAt = createdAt;
	}

	// For new quiz inserts
	public Quiz(long creatorId, String title, String description, boolean randomOrder, boolean onePage,
							boolean immediateCorrection, boolean practiceEnabled
	) {
		this.id = 0;
		this.creatorId = creatorId;
		this.title = title;
		this.description = description;
		this.randomOrder = randomOrder;
		this.onePage = onePage;
		this.immediateCorrection = immediateCorrection;
		this.practiceEnabled = practiceEnabled;
		this.createdAt = null;
	}

	public long getId() {
		return id;
	}

	public long getCreatorId() {
		return creatorId;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public boolean isRandomOrder() {
		return randomOrder;
	}

	public boolean isOnePage() {
		return onePage;
	}

	public boolean isImmediateCorrection() {
		return immediateCorrection;
	}

	public boolean isPracticeEnabled() {
		return practiceEnabled;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setRandomOrder(boolean randomOrder) {
		this.randomOrder = randomOrder;
	}

	public void setOnePage(boolean onePage) {
		this.onePage = onePage;
	}

	public void setImmediateCorrection(boolean immediateCorrection) {
		this.immediateCorrection = immediateCorrection;
	}

	public void setPracticeEnabled(boolean practiceEnabled) {
		this.practiceEnabled = practiceEnabled;
	}
}