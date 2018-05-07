var siteName = "";
	 $.ajax({
		    type : 'GET',
		    url:    '/rest/srvc/props',
		    dataType: 'json',
		    async: false, 
		    success:  function(json) {
		    	siteName = json['site.name'];
		    }
		    });	 

$(document).on(
		"click",
		"[ id |='nav' ] a",
		function(e) {

			try {
				e.preventDefault();
				e.stopImmediatePropagation();
			} catch (err) {
				// do nothing
			}

			var disabledValue = $(this).attr("data-disabled");

			if (disabledValue === "true" || disabledValue === "disabled") {
				return false;
			}

			var myHref = $(this).attr("href");
			var atitle = siteName + " - " + $(this).attr("title");

			try {
				$.ajax({
					type : 'GET',
					url : myHref + "?ajaxTemplate=1",
					async : true,
					success : function(response) {
						try {
							var html = $.parseHTML(response);
							var newcontent = $(html).filter('#content').html();
							$('#content').html(newcontent);

						} catch (err) {
							// do nothing
						}
					},
					error : function() {
						// do nothing
					}
				});

				window.history.pushState({url : myHref}, atitle, myHref);
				if (document.title !== atitle) {
					document.title = atitle;
				}
			} catch (err) {
				window.location.href = myHref;
			}
		});
