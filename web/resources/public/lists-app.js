var app = angular.module('listsApp', ['ui.router', 'listsAppControllers']);

// Use HTML5 history API
app.config(['$locationProvider', function($locationProvider) {
  $locationProvider.html5Mode(true);
}]);

// App states
app.config(['$stateProvider', '$urlRouterProvider', function($stateProvider, $urlRouterProvider) {
  $stateProvider
    .state('index', {
      url: '/',
      views: {
        'main': {
          template: "home page...",
          controller: 'IndexCtrl'
        }
      }
    })
    .state('about', {
      url: '/about',
      views: {
        'main': {
          template: "about page...",
          controller: 'AboutCtrl'
        }
      }
    })
    .state('contact', {
      url: '/contact',
      views: {
        'main': {
          template: "contact page...",
          controller: 'ContactCtrl'
        }
      }
    })
  $urlRouterProvider.otherwise('/'); // go here if nothing else matches
}]);

