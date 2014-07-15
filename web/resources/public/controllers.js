(function() {

  var m = angular.module('listsAppControllers', []);
  m.controller('IndexCtrl', ['$scope', '$state', function($scope, $state) {
  }]);
  m.controller('AboutCtrl', ['$scope', '$state', function($scope, $state) {
  }]);
  m.controller('ContactCtrl', ['$scope', '$state', function($scope, $state) {
  }]);
  m.controller('NavCtrl', ['$scope', '$state', function($scope, $state) {
    $scope.stateIncludes = function(x) { return $state.includes(x); };
  }]);

})();

