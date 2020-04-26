/// <reference types="cypress" />
/* eslint-disable no-undef*/

context('Actions', () => {
	beforeEach(() => {
		cy.visit('/');
	});

	//decision options
	it('creates a decision option', () => {
		addItemToList('decisionOptionsList', 'New Item');
	});
	it('edits a decision option', () => {
		editItemFromList('decisionOptionsList', 'New Item', 'Edited Item');
	});

	it("deletes decision option if it's left empty", () => {
		deleteItemWhenLeftEmpty('decisionOptionsList', 'New Item');
	});

	it('deletes a decision option', () => {
		deleteItemFromList('decisionOptionsList', 'New Item');
	});

	it('shows warning for not enough options', () => {
		showsWarningForNotEnoughItems('decisionOptionsList');
	});

	it('shows and hides decision options info', () => {
		showInfoDialog('decisionOptions', 'Decision Options');
	});

	it('shows decision options title', () => {
		cy.contains('Decision Options');
	});

	//selection criteria
	it('creates a selection criteria', () => {
		addItemToList('selectionCriteriaList', 'New Item');
	});

	it('edits a decision option', () => {
		editItemFromList('selectionCriteriaList', 'New Item', 'Edited Item');
	});

	it("deletes selection criteria if it's left empty", () => {
		deleteItemWhenLeftEmpty('decisionOptionsList', 'New Item');
	});

	it('deletes a selection criteria', () => {
		deleteItemFromList('selectionCriteriaList', 'New Item');
	});

	it('shows warning for not enough options', () => {
		showsWarningForNotEnoughItems('selectionCriteriaList');
	});

	it('shows and hides selection criteria info', () => {
		showInfoDialog('selectionCriteria', 'Selection Criteria');
	});

	it('shows selection criteria title', () => {
		cy.contains('Selection Criteria');
	});

	//functions
	const addItemToList = (listName: string, itemText: string) => {
		cy.getTestElement(listName).within(() => {
			cy
				.getTestElement('entryInput')
				.type(itemText)
				.getTestElement('addButton')
				.click()

				.getTestElement('entryInput')
				.should('have.value', '')

				.getTestElement('itemInput')
				.first()
				.should('have.value', itemText)

				.getTestElement('entryInput')
				.type(`${itemText} 1`)
				.type('{enter}')

				.getTestElement('entryInput')
				.should('have.value', '')

				.getTestElement('itemInput')
				.first()
				.should('have.value', `${itemText} 1`);
		});
	};

	const editItemFromList = (listName: string, itemText: string, newItemText: string) => {
		cy.getTestElement(listName).within(() => {
			cy
				.getTestElement('itemInput')
				.first()
				.clear()
				.type(newItemText)
				.blur()

				.getTestElement('itemInput')
				.first()
				.should('have.value', newItemText);
		});
	};

	const deleteItemWhenLeftEmpty = (listName: string, itemText: string) => {
		cy.getTestElement(listName).within(() => {
			cy
				.getTestElement('itemInput')
				.first()
				.clear()
				.blur()

				.contains(itemText)
				.should('not.exist');
		});
	};

	const deleteItemFromList = (listName: string, itemText: string) => {
		cy.getTestElement(listName).within(() => {
			cy
				.getTestElement('deleteButton0')
				.click()

				.getTestElement('itemInput')
				.should('not.have.value', itemText);
		});
	};

	const showsWarningForNotEnoughItems = (listName: string) => {
		cy
			.getTestElement(listName)
			.within(() => {
				cy.getTestElement('itemInput').each((element, index, list) => {
					const itemIndex = list.length - index - 1;
					cy
						.getTestElement(`deleteButton${itemIndex}`)
						.click()
						.getTestElement(`deleteButton${itemIndex}`)
						.should('have.length', 0);
				});
			})
			.getTestElement('warningAlert')
			.should('have.length', 1)

			.getTestElement(listName)
			.within(() => {
				cy.getTestElement('entryInput').type('Item 1').type('{enter}');
			})
			.getTestElement('warningAlert')
			.should('have.length', 1)

			.getTestElement(listName)
			.within(() => {
				cy.getTestElement('entryInput').type('Item 2').type('{enter}');
			})

			.getTestElement('warningAlert')
			.should('have.length', 0);
	};

	const showInfoDialog = (infoName: string, title: string) => {
		cy
			.getTestElement(`${infoName}InfoButton`)
			.click()

			.getTestElement('infoText')
			.contains(title)

			.getTestElement('infoCloseButton')
			.click()

			.getTestElement('infoText')
			.should('have.length', 0);
	};
});