import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_chart/chart/chart_pie_bean.dart';
import 'package:flutter_chart/chart/view/chart_pie.dart';
import 'package:flutter_chart/flutter_chart.dart';

///Demo : how to use Chart
class ChartPage extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      home: AnnotatedRegion(
        value: SystemUiOverlayStyle.light.copyWith(
            statusBarBrightness: Brightness.dark,
            systemNavigationBarColor: Colors.black),
        child: _ExampleChart(),
      ),
    );
  }
}

class _ExampleChart extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => _ChartState();
}

class _ChartState extends State<_ExampleChart> {
  bool _isCanTouch = false;
  bool _isCanReorder = false;
  List<Widget> children;

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      home: Scaffold(
        backgroundColor: Colors.black,
        appBar: _buildAppBar(),
        body: _generateWidget(),
      ),
    );
  }

  AppBar _buildAppBar() {
    return AppBar(
        backgroundColor: Colors.black,
        titleSpacing: 0,
        title: Row(
          mainAxisAlignment: MainAxisAlignment.end,
          crossAxisAlignment: CrossAxisAlignment.center,
          children: <Widget>[
            Text(
              _isCanTouch ? "关闭触摸" : "开启触摸",
              style: TextStyle(
                  color: _isCanTouch ? Colors.green : Colors.blueGrey),
            ),
            Switch(
                inactiveTrackColor: Colors.blueGrey,
                activeColor: Colors.green,
                value: _isCanTouch,
                onChanged: (value) {
                  if (_isCanTouch != value) {
                    setState(() {
                      _isCanTouch = !_isCanTouch;
                      if (_isCanTouch) _isCanReorder = false;
                      if (children != null) children.clear();
                    });
                  }
                }),
            Text(
              _isCanReorder ? "关闭拖动" : "开启拖动",
              style: TextStyle(
                  color: _isCanReorder ? Colors.green : Colors.blueGrey),
            ),
            Switch(
                inactiveTrackColor: Colors.blueGrey,
                activeColor: Colors.green,
                value: _isCanReorder,
                onChanged: (value) {
                  if (_isCanReorder != value) {
                    setState(() {
                      _isCanReorder = !_isCanReorder;
                      if (_isCanReorder) _isCanTouch = false;
                      if (children != null) children.clear();
                    });
                  }
                })
          ],
        ));
  }

  Widget _generateWidget() {
    if (children == null || children.length == 0) {
      children = new List();
      children.add(_buildChartBarCircle(context));
      children.add(_buildChartBarRound(context));
      children.add(_buildChartCurve(context));
      children.add(_buildChartLine(context));
      children.add(_buildChartPie(context));
    }
    if (_isCanReorder) {
      return ReorderableListView(
        reverse: false,
        scrollDirection: Axis.vertical,
        onReorder: (oldIndex, newIndex) {
          setState(() {
            if (newIndex > oldIndex) {
              newIndex -= 1;
            }
            var item = children.removeAt(oldIndex);
            children.insert(newIndex, item);
          });
        },
        children: children,
      );
    } else {
      return ListView(
        physics: BouncingScrollPhysics(),
        children: <Widget>[
          _buildChartBarCircle(context),
          _buildChartBarRound(context),
          _buildChartCurve(context),
          _buildChartLine(context),
          _buildChartPie(context),
        ],
      );
    }
  }

  ///curve
  Widget _buildChartCurve(context) {
    var chartLine = ChartLine(
      chartBeans: [
        ChartBean(x: '12-01', y: 30),
        ChartBean(x: '12-02', y: 88),
        ChartBean(x: '12-03', y: 20),
        ChartBean(x: '12-04', y: 67),
        ChartBean(x: '12-05', y: 10),
        ChartBean(x: '12-06', y: 40),
        ChartBean(x: '12-07', y: 10),
      ],
      size: Size(MediaQuery.of(context).size.width,
          MediaQuery.of(context).size.height / 5 * 1.6),
      isCurve: true,
      lineWidth: 4,
      lineColor: Colors.blueAccent,
      fontColor: Colors.white,
      xyColor: Colors.white,
      shaderColors: [
        Colors.blueAccent.withOpacity(0.3),
        Colors.blueAccent.withOpacity(0.1)
      ],
      fontSize: 12,
      yNum: 8,
      isAnimation: true,
      isReverse: false,
      isCanTouch: _isCanTouch,
      isShowPressedHintLine: true,
      pressedPointRadius: 4,
      pressedHintLineWidth: 0.5,
      pressedHintLineColor: Colors.white,
      duration: Duration(milliseconds: 2000),
    );
    return Card(
      key: Key("Curve${DateTime.now().millisecondsSinceEpoch}"),
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
      margin: EdgeInsets.only(left: 16, right: 16, top: 8, bottom: 8),
      semanticContainer: true,
      color: Colors.green.withOpacity(0.5),
      child: chartLine,
      clipBehavior: Clip.antiAlias,
    );
  }

  ///line
  Widget _buildChartLine(context) {
    var chartLine = ChartLine(
      chartBeans: [
        ChartBean(x: '12-01', y: 30),
        ChartBean(x: '12-02', y: 88),
        ChartBean(x: '12-03', y: 20),
        ChartBean(x: '12-04', y: 67),
        ChartBean(x: '12-05', y: 10),
        ChartBean(x: '12-06', y: 40),
        ChartBean(x: '12-07', y: 10),
      ],
      size: Size(MediaQuery.of(context).size.width,
          MediaQuery.of(context).size.height / 5 * 1.6),
      isCurve: false,
      lineWidth: 2,
      lineColor: Colors.yellow,
      fontColor: Colors.white,
      xyColor: Colors.white,
      shaderColors: [
        Colors.yellow.withOpacity(0.3),
        Colors.yellow.withOpacity(0.1)
      ],
      fontSize: 12,
      yNum: 8,
      isAnimation: true,
      isReverse: false,
      isCanTouch: _isCanTouch,
      isShowPressedHintLine: true,
      pressedPointRadius: 4,
      pressedHintLineWidth: 0.5,
      pressedHintLineColor: Colors.white,
      duration: Duration(milliseconds: 2000),
    );
    return Card(
      key: Key("Line${DateTime.now().millisecondsSinceEpoch}"),
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
      margin: EdgeInsets.only(left: 16, right: 16, top: 8, bottom: 8),
      semanticContainer: true,
      color: Colors.yellow.withOpacity(0.4),
      child: chartLine,
      clipBehavior: Clip.antiAlias,
    );
  }

  ///bar-circle
  Widget _buildChartBarCircle(context) {
    var chartBar = ChartBar(
      chartBeans: [
        ChartBean(x: '12-01', y: 30, color: Colors.red),
        ChartBean(x: '12-02', y: 100, color: Colors.yellow),
        ChartBean(x: '12-03', y: 70, color: Colors.green),
        ChartBean(x: '12-04', y: 70, color: Colors.blue),
        ChartBean(x: '12-05', y: 30, color: Colors.deepPurple),
        ChartBean(x: '12-06', y: 90, color: Colors.deepOrange),
        ChartBean(x: '12-07', y: 50, color: Colors.greenAccent)
      ],
      size: Size(MediaQuery.of(context).size.width,
          MediaQuery.of(context).size.height / 5 * 1.8),
      rectColor: Colors.deepPurple,
      isShowX: true,
      fontColor: Colors.white,
      rectShadowColor: Colors.white.withOpacity(0.5),
      isReverse: false,
      isCanTouch: _isCanTouch,
      isShowTouchShadow: true,
      isShowTouchValue: true,
      rectRadiusTopLeft: 50,
      rectRadiusTopRight: 50,
      duration: Duration(milliseconds: 1000),
    );
    return Card(
      key: Key("BarCircle${DateTime.now().millisecondsSinceEpoch}"),
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
      margin: EdgeInsets.only(left: 16, right: 16, top: 8, bottom: 8),
      semanticContainer: true,
      color: Colors.blue.withOpacity(0.4),
      child: chartBar,
      clipBehavior: Clip.antiAlias,
    );
  }

  ///bar-round
  Widget _buildChartBarRound(context) {
    var chartBar = ChartBar(
      chartBeans: [
        ChartBean(x: '12-01', y: 30, color: Colors.red),
        ChartBean(x: '12-02', y: 100, color: Colors.yellow),
        ChartBean(x: '12-03', y: 70, color: Colors.green),
        ChartBean(x: '12-04', y: 70, color: Colors.blue),
        ChartBean(x: '12-05', y: 30, color: Colors.deepPurple),
        ChartBean(x: '12-06', y: 90, color: Colors.deepOrange),
        ChartBean(x: '12-07', y: 50, color: Colors.greenAccent)
      ],
      size: Size(MediaQuery.of(context).size.width,
          MediaQuery.of(context).size.height / 5 * 1.8),
      rectColor: Colors.deepPurple,
      isShowX: true,
      fontColor: Colors.white,
      rectShadowColor: Colors.white.withOpacity(0.5),
      isReverse: false,
      isCanTouch: _isCanTouch,
      isShowTouchShadow: true,
      isShowTouchValue: true,
      rectRadiusTopLeft: 4,
      rectRadiusTopRight: 4,
      duration: Duration(milliseconds: 1000),
    );
    return Card(
      key: Key("BarRound${DateTime.now().millisecondsSinceEpoch}"),
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
      margin: EdgeInsets.only(left: 16, right: 16, top: 8, bottom: 8),
      semanticContainer: true,
      color: Colors.brown.withOpacity(0.6),
      child: chartBar,
      clipBehavior: Clip.antiAlias,
    );
  }

  ///pie
  Widget _buildChartPie(context) {
    var chartPie = ChartPie(
      chartBeans: [
        ChartPieBean(type: '话费', value: 30, color: Colors.blueGrey),
        ChartPieBean(type: '零食', value: 120, color: Colors.deepPurple),
        ChartPieBean(type: '衣服', value: 60, color: Colors.green),
        ChartPieBean(type: '早餐', value: 60, color: Colors.blue),
        ChartPieBean(type: '水果', value: 30, color: Colors.red),
      ],
      size: Size(
          MediaQuery.of(context).size.width, MediaQuery.of(context).size.width),
      R: MediaQuery.of(context).size.width / 3,
      centerR: 6,
      duration: Duration(milliseconds: 2000),
      centerColor: Colors.white,
      fontColor: Colors.white,
    );
    return Card(
      key: Key("Pie${DateTime.now().millisecondsSinceEpoch}"),
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
      margin: EdgeInsets.only(left: 16, right: 16, top: 8, bottom: 8),
      color: Colors.orangeAccent.withOpacity(0.6),
      clipBehavior: Clip.antiAlias,
      borderOnForeground: true,
      child: chartPie,
    );
  }
}
