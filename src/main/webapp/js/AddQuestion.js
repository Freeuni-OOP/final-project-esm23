/**
 * addQuestion.js — dynamic behaviour for the Add Question form
 *
 * Responsibilities:
 *  - Show/hide the correct input section based on selected question type
 *  - Allow adding/removing multiple accepted-answer slots (fill-blank / question-response)
 *  - Allow adding/removing MC options beyond the default 4
 *  - Keep radio-button correct-option indices in sync when rows are reordered
 */

$(function () {

    /* ------------------------------------------------------------------ */
    /* Helpers                                                              */
    /* ------------------------------------------------------------------ */

    function showSection(id) {
        $('#sectionTextAnswer, #sectionMultipleChoice, #sectionPicture').hide();
        if (id) $('#' + id).show();
    }

    /* ------------------------------------------------------------------ */
    /* Type-switcher                                                        */
    /* ------------------------------------------------------------------ */

    function updateForm() {
        var type = $('#questionType').val();
        if (type === 'QUESTION_RESPONSE' || type === 'FILL_BLANK') {
            showSection('sectionTextAnswer');
        } else if (type === 'MULTIPLE_CHOICE') {
            showSection('sectionMultipleChoice');
        } else if (type === 'PICTURE_RESPONSE') {
            showSection('sectionPicture');
        } else {
            showSection(null);
        }
    }

    $('#questionType').on('change', updateForm);
    updateForm(); // run once on load


    /* ------------------------------------------------------------------ */
    /* Multiple accepted answers (fill-blank / question-response)          */
    /* ------------------------------------------------------------------ */

    var answerCount = 1; // tracks suffix for name attributes

    function makeAnswerSlot(index, value) {
        value = value || '';
        return $('<div class="answer-slot">')
            .append(
                $('<input>', {
                    type: 'text',
                    name: 'correctAnswer',   // servlet collects all values with this name
                    class: 'form-control',
                    placeholder: 'Accepted answer ' + index,
                    value: value
                })
            )
            .append(
                $('<button>', {
                    type: 'button',
                    class: 'btn-remove-answer',
                    title: 'Remove this answer',
                    html: '&times;'
                }).on('click', function () {
                    var $slots = $('#answerSlots .answer-slot');
                    if ($slots.length > 1) {
                        $(this).closest('.answer-slot').remove();
                        renumberAnswerSlots();
                    }
                })
            );
    }

    function renumberAnswerSlots() {
        $('#answerSlots .answer-slot').each(function (i) {
            $(this).find('input').attr('placeholder', 'Accepted answer ' + (i + 1));
        });
    }

    // Wrap the existing correctAnswer input in the slot structure
    var $existingInput = $('input[name="correctAnswer"]').first();
    if ($existingInput.length) {
        var $slotsContainer = $('<div id="answerSlots">');
        var $slot = makeAnswerSlot(1);
        $slot.find('input').val($existingInput.val());
        $existingInput.replaceWith($slotsContainer.append($slot));
    }

    $('#btnAddAnswer').on('click', function () {
        answerCount++;
        $('#answerSlots').append(makeAnswerSlot(answerCount));
    });


    /* ------------------------------------------------------------------ */
    /* Multiple-Choice: add / remove options                               */
    /* ------------------------------------------------------------------ */

    function rebuildMCIndices() {
        $('#mcOptions .mc-option-row').each(function (i) {
            $(this).find('input[type="radio"]').val(i);
            $(this).find('input[type="text"]').attr('placeholder', 'Option ' + String.fromCharCode(65 + i)); // A, B, C…
        });
    }

    function makeMCOptionRow(index, value) {
        var label = String.fromCharCode(65 + index); // A, B, C, D…
        return $('<div class="mc-option-row">')
            .append(
                $('<input>', {
                    type: 'radio',
                    name: 'correctOption',
                    value: index,
                    checked: index === 0
                })
            )
            .append(
                $('<input>', {
                    type: 'text',
                    name: 'option' + index,
                    class: 'form-control',
                    placeholder: 'Option ' + label,
                    value: value || ''
                })
            )
            .append(
                $('<button>', {
                    type: 'button',
                    class: 'btn-remove-option',
                    title: 'Remove option',
                    html: '&times;'
                }).on('click', function () {
                    var $rows = $('#mcOptions .mc-option-row');
                    if ($rows.length <= 2) return; // keep at least 2
                    $(this).closest('.mc-option-row').remove();
                    rebuildMCIndices();
                })
            );
    }

    $('#btnAddOption').on('click', function () {
        var count = $('#mcOptions .mc-option-row').length;
        if (count >= 8) return; // reasonable cap
        $('#mcOptions').append(makeMCOptionRow(count));
        rebuildMCIndices();
    });

    // The servlet expects name="option0"…"option3" — we need to keep those in
    // sync when rows move. After any radio change, also fire rebuildMCIndices.
    $('#mcOptions').on('change', 'input[type="radio"]', function () {
        rebuildMCIndices();
        // make sure the chosen radio stays checked after re-indexing
        $(this).prop('checked', true);
    });


    /* ------------------------------------------------------------------ */
    /* Image preview (PICTURE_RESPONSE)                                    */
    /* ------------------------------------------------------------------ */

    $('#imageUrl').on('input', function () {
        var url = $(this).val().trim();
        var $preview = $('#imagePreview');
        if (url) {
            $preview.attr('src', url).show();
        } else {
            $preview.hide();
        }
    });

    // Also trigger on page load in case of a browser-cached value
    if ($('#imageUrl').val()) {
        $('#imageUrl').trigger('input');
    }


    /* ------------------------------------------------------------------ */
    /* Form validation before submit                                       */
    /* ------------------------------------------------------------------ */

    $('form#addQuestionForm').on('submit', function (e) {
        var type = $('#questionType').val();

        if (type === 'MULTIPLE_CHOICE') {
            var hasEmpty = false;
            $('#mcOptions .mc-option-row input[type="text"]').each(function () {
                if ($(this).val().trim() === '') { hasEmpty = true; return false; }
            });
            if (hasEmpty) {
                e.preventDefault();
                alert('Please fill in all Multiple Choice options before saving.');
                return false;
            }
        }

        if (type === 'QUESTION_RESPONSE' || type === 'FILL_BLANK') {
            var hasAnswer = false;
            $('#answerSlots input[name="correctAnswer"]').each(function () {
                if ($(this).val().trim() !== '') { hasAnswer = true; return false; }
            });
            if (!hasAnswer) {
                e.preventDefault();
                alert('Please enter at least one correct answer.');
                return false;
            }
        }

        if (type === 'PICTURE_RESPONSE') {
            var imgUrl = $('#imageUrl').val().trim();
            var ans    = $('input[name="correctAnswerPicture"]').val().trim();
            if (!imgUrl) {
                e.preventDefault();
                alert('Please enter an image URL.');
                return false;
            }
            if (!ans) {
                e.preventDefault();
                alert('Please enter the correct answer for the picture question.');
                return false;
            }
        }
    });

});

