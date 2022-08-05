/*------------------------------------------------------------------------------------
    
JS INDEX
=============

01 - POST SILDER JS
02 - Btn To Top JS
03 - Responsive Menu



-------------------------------------------------------------------------------------*/


(function ($) {
	"use strict";

    jQuery(document).ready(function($){
        
        /* 
        =================================================================
        01 - POST SILDER JS
        =================================================================	
        */
        
        $(".post-slider").owlCarousel({
            center:false,
            autoplay:true,
            loop:true,
            autoplaySpeed:1200,
            autoplayTimeout:11000,
            margin:0,
            touchDrag:false,
            mouseDrag:true,
            dots: false,
            nav:true,
            navText: ["<i class='fa fa-angle-left'></i>",
                  "<i class='fa fa-angle-right'></i>"],
            responsive:{
                0: {
                    items: 1
                },
                480: {
                    items: 1
                },
                600: {
                    items: 2
                },
                1000: {
                    items: 3
                },
                1200: {
                    items: 3
                }
            }
        });
        
        /* 
        =================================================================
        02 - Btn To Top JS
        =================================================================	
        */
        if ($("body").length) {
            var btnUp = $('<div/>', {
                'class': 'btntoTop'
            });
            btnUp.appendTo('body');
            $(document).on('click', '.btntoTop', function() {
                $('html, body').animate({
                    scrollTop: 0
                }, 700);
            });
            $(window).on('scroll', function() {
                if ($(this).scrollTop() > 200) $('.btntoTop').addClass('active');
                else $('.btntoTop').removeClass('active');
            });
        }
        
        
        /* 
        =================================================================
        03 - Responsive Menu
        =================================================================	
        */
        $("ul#rapid_navigation").slicknav({
            prependTo: ".rapid-responsive-menu"
        });
        
        
        

    });
}(jQuery));	
