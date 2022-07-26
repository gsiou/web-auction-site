init_jssor_slider = function (containerId) {

    var options = {
        $AutoPlay: true,                                   //[Optional] Whether to auto play, to enable slideshow, this option must be set to true, default value is false
        $SlideDuration: 500,                                //[Optional] Specifies default duration (swipe) for slide in milliseconds, default value is 500

        $BulletNavigatorOptions: {                                //[Optional] Options to specify and enable navigator or not
            $Class: $JssorBulletNavigator$,                       //[Required] Class to create navigator instance
            $ChanceToShow: 2,                               //[Required] 0 Never, 1 Mouse Over, 2 Always
            $AutoCenter: 1,                                 //[Optional] Auto center navigator in parent container, 0 None, 1 Horizontal, 2 Vertical, 3 Both, default value is 0
            $Steps: 1,                                      //[Optional] Steps to go for each navigation request, default value is 1
            $Rows: 1,                                      //[Optional] Specify lanes to arrange items, default value is 1
            $SpacingX: 8,                                   //[Optional] Horizontal space between each item in pixel, default value is 0
            $SpacingY: 8,                                   //[Optional] Vertical space between each item in pixel, default value is 0
            $Orientation: 1                                 //[Optional] The orientation of the navigator, 1 horizontal, 2 vertical, default value is 1
        }
    };

    var jssor_slider = new $JssorSlider$(containerId, options);
    
  //responsive code begin
  //you can remove responsive code if you don't want the slider scales while window resizes
    function ScaleSlider() {
        var parentWidth = jssor_slider.$Elmt.parentNode.clientWidth;
        if (parentWidth)
            jssor_slider.$ScaleWidth(Math.min(parentWidth*0.45,480));
        else
            window.setTimeout(ScaleSlider, 30);
    }
    ScaleSlider();

    $(window).bind("load", ScaleSlider);
    $(window).bind("resize", ScaleSlider);
    $(window).bind("orientationchange", ScaleSlider);
}