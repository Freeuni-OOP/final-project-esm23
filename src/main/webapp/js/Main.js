/**
 * main.js — site-wide JavaScript for Quiz Website
 * Stack: vanilla Servlets/JSP, jQuery, MySQL, Maven
 */

$(function () {

    /* ------------------------------------------------------------------ */
    /* 1. MC option click — make the whole row clickable                    */
    /* ------------------------------------------------------------------ */
    $(document).on('click', '.mc-option', function () {
        var $radio = $(this).find('input[type="radio"]');
        $radio.prop('checked', true).trigger('change');
    });

    $(document).on('change', '.mc-option input[type="radio"]', function () {
        // Deselect siblings
        $(this).closest('form').find('.mc-option').removeClass('selected');
        $(this).closest('.mc-option').addClass('selected');
    });

    // Restore selected state on page load (browser back-button cache)
    $('.mc-option input[type="radio"]:checked').each(function () {
        $(this).closest('.mc-option').addClass('selected');
    });


    /* ------------------------------------------------------------------ */
    /* 2. Quiz timer (elapsed, shown on take-quiz pages)                   */
    /* ------------------------------------------------------------------ */
    var $timer = $('#quiz-timer');
    if ($timer.length) {
        var startTs = parseInt($timer.data('start'), 10) || Date.now();
        function tick() {
            var elapsed = Math.floor((Date.now() - startTs) / 1000);
            var m = Math.floor(elapsed / 60);
            var s = elapsed % 60;
            $timer.text(
                (m < 10 ? '0' : '') + m + ':' + (s < 10 ? '0' : '') + s
            );
        }
        tick();
        setInterval(tick, 1000);
    }


    /* ------------------------------------------------------------------ */
    /* 3. One-page quiz: disable submit until every question has an answer  */
    /* ------------------------------------------------------------------ */
    var $quizForm = $('#quiz-one-page-form');
    if ($quizForm.length) {
        var $submitBtn = $quizForm.find('[data-quiz-submit]');

        function checkAllAnswered() {
            var allDone = true;

            // Check every required text input
            $quizForm.find('input[type="text"][data-required]').each(function () {
                if ($(this).val().trim() === '') { allDone = false; return false; }
            });

            // Check every radio group (each group name must have a checked radio)
            var radioGroups = {};
            $quizForm.find('input[type="radio"]').each(function () {
                radioGroups[this.name] = radioGroups[this.name] || false;
                if (this.checked) radioGroups[this.name] = true;
            });
            $.each(radioGroups, function (_, answered) {
                if (!answered) { allDone = false; return false; }
            });

            $submitBtn.prop('disabled', !allDone);
        }

        $quizForm.on('input change', 'input', checkAllAnswered);
        checkAllAnswered();
    }


    /* ------------------------------------------------------------------ */
    /* 4. Results page: animate score progress bar                         */
    /* ------------------------------------------------------------------ */
    var $fill = $('.progress-bar-fill[data-pct]');
    if ($fill.length) {
        var pct = parseInt($fill.data('pct'), 10) || 0;
        // Start at 0, animate to pct
        $fill.css('width', '0%');
        setTimeout(function () {
            $fill.css({ width: pct + '%', transition: 'width .8s ease' });
        }, 100);
    }


    /* ------------------------------------------------------------------ */
    /* 5. Auto-dismiss flash alerts after 5 s                              */
    /* ------------------------------------------------------------------ */
    var $alerts = $('.alert[data-auto-dismiss]');
    if ($alerts.length) {
        setTimeout(function () {
            $alerts.fadeOut(400, function () { $(this).remove(); });
        }, 5000);
    }


    /* ------------------------------------------------------------------ */
    /* 6. Confirm destructive actions                                      */
    /* ------------------------------------------------------------------ */
    $(document).on('click', '[data-confirm]', function (e) {
        var msg = $(this).data('confirm') || 'Are you sure?';
        if (!confirm(msg)) {
            e.preventDefault();
            return false;
        }
    });


    /* ------------------------------------------------------------------ */
    /* 7. Navbar active link highlight                                     */
    /* ------------------------------------------------------------------ */
    var path = window.location.pathname;
    $('.navbar-nav a').each(function () {
        var href = $(this).attr('href') || '';
        if (href && path.indexOf(href) !== -1 && href !== '/') {
            $(this).addClass('active').css({
                background: 'rgba(255,255,255,.2)',
                color: '#fff'
            });
        }
    });

});
